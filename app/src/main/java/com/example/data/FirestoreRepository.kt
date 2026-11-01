package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreRepository(
    val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (!snapshot.exists()) return@map null
                    val data = snapshot.data ?: return@map null
                    val coins = (data["coins"] as? Number)?.toLong() ?: 50000L
                    val diamonds = (data["diamonds"] as? Number)?.toLong() ?: 1000L
                    val vip = (data["vipLevel"] as? Number)?.toInt() ?: 1
                    val hasSm = data["hasSoulmate"] as? Boolean ?: false
                    val smPartner = data["soulmatePartner"] as? String ?: ""
                    val smTierStr = data["soulmateTier"] as? String ?: RingTier.DIAMOND.name
                    val smIntimacy = (data["soulmateIntimacy"] as? Number)?.toInt() ?: 0
                    val famName = data["familyName"] as? String ?: "Royal Warriors"

                    val soulmate = if (hasSm && smPartner.isNotBlank()) {
                        SoulmateInfo(
                            partnerName = smPartner,
                            partnerAvatar = "💖",
                            ringTier = try { RingTier.valueOf(smTierStr) } catch (_: Exception) { RingTier.DIAMOND },
                            intimacyScore = smIntimacy,
                            daysTogether = 12,
                            confessionNote = "Eternal love bound by Soulmate Ring 💍"
                        )
                    } else null

                    UserProfile(
                        id = userId,
                        name = data["displayName"] as? String ?: "PartyUser",
                        avatar = data["avatar"] as? String ?: "👑",
                        loginMethod = LoginMethod.GMAIL,
                        email = auth.currentUser?.email,
                        coins = coins,
                        diamonds = diamonds,
                        vipLevel = vip,
                        soulmate = soulmate,
                        family = FamilyMembership(
                            familyId = "FAM_ROYAL",
                            familyName = famName,
                            familyBadge = "⚔️",
                            role = "Member",
                            familyLevel = 6,
                            weeklyExp = 28400
                        )
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    throw error
                }
        )
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val payload = mutableMapOf<String, Any?>(
            "userId" to uid,
            "displayName" to profile.name.take(50),
            "avatar" to profile.avatar.take(200),
            "coins" to profile.coins,
            "diamonds" to profile.diamonds,
            "vipLevel" to profile.vipLevel,
            "hasSoulmate" to (profile.soulmate != null),
            "soulmatePartner" to (profile.soulmate?.partnerName ?: ""),
            "soulmateTier" to (profile.soulmate?.ringTier?.name ?: ""),
            "soulmateIntimacy" to (profile.soulmate?.intimacyScore ?: 0),
            "familyName" to (profile.family?.familyName ?: "Royal Warriors"),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        val existing = docRef.get().await()
        if (!existing.exists()) {
            payload["createdAt"] = FieldValue.serverTimestamp()
        } else {
            existing.get("createdAt")?.let { payload["createdAt"] = it }
        }

        val cleanPayload = payload.filterValues { it != null }
        try {
            docRef.set(cleanPayload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    fun observeRooms(): Flow<List<PartyRoomData>> = flow {
        val path = "rooms"
        emitAll(
            db.collection("rooms")
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        val data = doc.data ?: return@mapNotNull null
                        PartyRoomData(
                            id = doc.id,
                            title = data["title"] as? String ?: "Party Room",
                            hostName = data["hostName"] as? String ?: "Host",
                            hostAvatar = data["hostAvatar"] as? String ?: "🎙️",
                            tag = data["tag"] as? String ?: "🎉 Party",
                            popularity = (data["popularity"] as? Number)?.toInt() ?: 12000,
                            isFamilyRoom = data["isFamilyRoom"] as? Boolean ?: false,
                            announcement = data["announcement"] as? String ?: "Welcome to the party room!",
                            seats = (1..8).map { i ->
                                when (i) {
                                    1 -> MicSeat(1, data["hostId"] as? String, data["hostName"] as? String, data["hostAvatar"] as? String, false, true)
                                    else -> MicSeat(i)
                                }
                            }
                        )
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createRoom(room: PartyRoomData) {
        val uid = requireUserId()
        val docRef = db.collection("rooms").document(room.id)
        val payload = mapOf(
            "roomId" to room.id,
            "title" to room.title.take(100),
            "hostId" to uid,
            "hostName" to room.hostName.take(50),
            "hostAvatar" to room.hostAvatar.take(20),
            "tag" to room.tag.take(30),
            "announcement" to room.announcement.take(200),
            "popularity" to room.popularity,
            "isFamilyRoom" to room.isFamilyRoom,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    fun observeMessages(roomId: String): Flow<List<ChatMessage>> = flow {
        val path = "rooms/$roomId/messages"
        emitAll(
            db.collection("rooms").document(roomId).collection("messages")
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { doc ->
                        val data = doc.data ?: return@mapNotNull null
                        ChatMessage(
                            id = doc.id,
                            senderId = data["senderId"] as? String ?: "",
                            senderName = data["senderName"] as? String ?: "User",
                            senderAvatar = data["senderAvatar"] as? String ?: "👤",
                            text = data["text"] as? String ?: "",
                            isSystem = data["isSystem"] as? Boolean ?: false,
                            isGift = data["isGift"] as? Boolean ?: false,
                            giftEmoji = data["giftEmoji"] as? String ?: ""
                        )
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun sendMessage(
        roomId: String,
        text: String,
        senderName: String,
        senderAvatar: String,
        isGift: Boolean = false,
        giftEmoji: String = ""
    ) {
        val uid = requireUserId()
        val msgId = "msg_${UUID.randomUUID().toString().take(12)}"
        val docRef = db.collection("rooms").document(roomId).collection("messages").document(msgId)
        val payload = mapOf(
            "messageId" to msgId,
            "roomId" to roomId,
            "senderId" to uid,
            "senderName" to senderName.take(50),
            "senderAvatar" to senderAvatar.take(20),
            "text" to text.take(500),
            "isSystem" to false,
            "isGift" to isGift,
            "giftEmoji" to giftEmoji.take(20),
            "createdAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun submitReport(targetType: String, targetId: String, reason: String, details: String): ReportTicket {
        val uid = requireUserId()
        val reportId = "rep_${UUID.randomUUID().toString().take(12)}"
        val docRef = db.collection("reports").document(reportId)
        val payload = mapOf(
            "reportId" to reportId,
            "reporterId" to uid,
            "targetType" to targetType.take(30),
            "targetId" to targetId.take(100),
            "reason" to reason.take(100),
            "details" to details.take(500),
            "status" to "Under Review",
            "createdAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }

        return ReportTicket(
            ticketId = reportId,
            targetType = targetType,
            targetId = targetId,
            reason = reason,
            details = details,
            status = "Under Review",
            timestamp = System.currentTimeMillis()
        )
    }
}
