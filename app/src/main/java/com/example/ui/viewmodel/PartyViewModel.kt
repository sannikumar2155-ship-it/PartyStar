package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatMessage
import com.example.data.FamilyRoomData
import com.example.data.FirestoreRepository
import com.example.data.GameType
import com.example.data.LoginMethod
import com.example.data.PartyRepository
import com.example.data.PartyRoomData
import com.example.data.PaymentOption
import com.example.data.RechargePack
import com.example.data.RechargeReceipt
import com.example.data.ReportTicket
import com.example.data.RingTier
import com.example.data.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PartyViewModel(
    application: Application
) : AndroidViewModel(application) {

    val repository: PartyRepository = PartyRepository()
    private val firestoreRepo: FirestoreRepository? = try {
        FirestoreRepository(application)
    } catch (e: Exception) {
        Log.w("PartyViewModel", "Firestore not initialized: ${e.message}")
        null
    }

    val currentUser: StateFlow<UserProfile?> = repository.currentUser
    val rooms: StateFlow<List<PartyRoomData>> = repository.rooms
    val familyRooms: StateFlow<List<FamilyRoomData>> = repository.familyRooms
    val activeRoom: StateFlow<PartyRoomData?> = repository.activeRoom
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    val floatingEmojis = repository.floatingEmojis
    val reports: StateFlow<List<ReportTicket>> = repository.reports
    val transactions: StateFlow<List<RechargeReceipt>> = repository.transactions

    // Dialog & overlay states
    private val _activeGame = MutableStateFlow<GameType?>(null)
    val activeGame: StateFlow<GameType?> = _activeGame.asStateFlow()

    private val _showRechargeSheet = MutableStateFlow(false)
    val showRechargeSheet: StateFlow<Boolean> = _showRechargeSheet.asStateFlow()

    private val _showSoulmateSheet = MutableStateFlow(false)
    val showSoulmateSheet: StateFlow<Boolean> = _showSoulmateSheet.asStateFlow()

    private val _showReportSheet = MutableStateFlow(false)
    val showReportSheet: StateFlow<Boolean> = _showReportSheet.asStateFlow()

    private val _showFeedbackSheet = MutableStateFlow(false)
    val showFeedbackSheet: StateFlow<Boolean> = _showFeedbackSheet.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    init {
        // Real-time synchronization with Firebase Auth & Firestore
        val auth = Firebase.auth
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                viewModelScope.launch {
                    try {
                        val uid = firebaseUser.uid
                        val current = currentUser.value
                        val initialProfile = UserProfile(
                            id = uid,
                            name = firebaseUser.displayName ?: current?.name ?: "StarUser_${uid.take(4)}",
                            avatar = current?.avatar ?: "👑",
                            loginMethod = LoginMethod.GMAIL,
                            email = firebaseUser.email,
                            phone = firebaseUser.phoneNumber,
                            coins = current?.coins ?: 50000L,
                            diamonds = current?.diamonds ?: 1200L,
                            vipLevel = current?.vipLevel ?: 2
                        )
                        firestoreRepo?.saveUserProfile(initialProfile)
                        firestoreRepo?.observeUserProfile(uid)?.collectLatest { profile ->
                            if (profile != null) {
                                repository.setCurrentUser(profile)
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("PartyViewModel", "Firestore user sync warning: ${e.message}")
                    }
                }

                // Auth-gated observation of party rooms from Firestore
                viewModelScope.launch {
                    try {
                        firestoreRepo?.observeRooms()?.collectLatest { roomList ->
                            if (roomList.isNotEmpty()) {
                                repository.setRooms(roomList)
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("PartyViewModel", "Firestore rooms stream warning: ${e.message}")
                    }
                }
            }
        }

        // Room audio/chat bot simulation
        viewModelScope.launch {
            val randomQuotes = listOf(
                "Super vibes here! 🎶",
                "Who is playing 777 right now?",
                "Rocket launch is pumping! 🚀",
                "Sending love to the stage! ❤️",
                "Nice voice host! Keep singing 🎤",
                "Just upgraded my Soulmate Ring! 💍"
            )
            while (true) {
                delay(12000)
                if (activeRoom.value != null && currentUser.value != null) {
                    val botQuote = randomQuotes.random()
                    repository.sendChatMessage(botQuote)
                }
            }
        }
    }

    fun openGame(game: GameType) {
        _activeGame.value = game
    }

    fun closeGame() {
        _activeGame.value = null
    }

    fun toggleRechargeSheet(show: Boolean) {
        _showRechargeSheet.value = show
    }

    fun toggleSoulmateSheet(show: Boolean) {
        _showSoulmateSheet.value = show
    }

    fun toggleReportSheet(show: Boolean) {
        _showReportSheet.value = show
    }

    fun toggleFeedbackSheet(show: Boolean) {
        _showFeedbackSheet.value = show
    }

    fun login(method: LoginMethod, identifier: String, name: String) {
        repository.login(method, identifier, name)
        emitMessage("Logged in successfully via ${method.label}! 🌟")
    }

    fun logout() {
        Firebase.auth.signOut()
        repository.logout()
        emitMessage("Logged out.")
    }

    fun joinRoom(room: PartyRoomData) {
        repository.joinRoom(room)
        if (firestoreRepo != null && Firebase.auth.currentUser != null) {
            viewModelScope.launch {
                try {
                    firestoreRepo.observeMessages(room.id).collectLatest { msgs ->
                        if (msgs.isNotEmpty()) {
                            repository.setChatMessages(msgs)
                        }
                    }
                } catch (e: Exception) {
                    Log.w("PartyViewModel", "Room message stream error: ${e.message}")
                }
            }
        }
    }

    fun leaveRoom() {
        repository.leaveRoom()
        _activeGame.value = null
    }

    fun occupyMicSeat(index: Int) {
        repository.occupyMicSeat(index)
        emitMessage("You are now on Mic Seat #$index 🎤")
    }

    fun toggleMicMute() {
        repository.toggleMicMute()
    }

    fun toggleTalking(talking: Boolean) {
        repository.toggleTalking(talking)
    }

    fun leaveMicSeat() {
        repository.leaveMicSeat()
        emitMessage("Stepped down from microphone.")
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val current = currentUser.value ?: return
        val room = activeRoom.value
        repository.sendChatMessage(text.trim())

        if (room != null && firestoreRepo != null && Firebase.auth.currentUser != null) {
            viewModelScope.launch {
                try {
                    firestoreRepo.sendMessage(
                        roomId = room.id,
                        text = text.trim(),
                        senderName = current.name,
                        senderAvatar = current.avatar
                    )
                } catch (e: Exception) {
                    Log.w("PartyViewModel", "Message write error: ${e.message}")
                }
            }
        }
    }

    fun triggerFloatingEmoji(emoji: String) {
        repository.triggerFloatingEmoji(emoji)
    }

    fun updateCoins(delta: Long): Boolean {
        val success = repository.updateCoins(delta)
        if (success) {
            currentUser.value?.let { user ->
                if (firestoreRepo != null && Firebase.auth.currentUser != null) {
                    viewModelScope.launch {
                        try {
                            firestoreRepo.saveUserProfile(user)
                        } catch (e: Exception) {
                            Log.w("PartyViewModel", "Coin sync error: ${e.message}")
                        }
                    }
                }
            }
        }
        return success
    }

    fun processRecharge(pack: RechargePack, method: PaymentOption) {
        viewModelScope.launch {
            try {
                val receipt = repository.processRecharge(pack, method)
                emitMessage("₹${receipt.amountInr} recharge successful! Added ${receipt.coinsAdded} Coins 💰")
                _showRechargeSheet.value = false
            } catch (e: Exception) {
                emitMessage("Recharge failed: ${e.message}")
            }
        }
    }

    fun upgradeSoulmateRing(tier: RingTier, partnerName: String) {
        val success = repository.upgradeSoulmateRing(tier, partnerName)
        if (success) {
            currentUser.value?.let { user ->
                if (firestoreRepo != null && Firebase.auth.currentUser != null) {
                    viewModelScope.launch {
                        try {
                            firestoreRepo.saveUserProfile(user)
                        } catch (e: Exception) {
                            Log.w("PartyViewModel", "Soulmate save error: ${e.message}")
                        }
                    }
                }
            }
            emitMessage("Congratulations! Bound with ${tier.title} 💍✨")
        } else {
            emitMessage("Insufficient coins to forge this ring! Recharge coins.")
        }
    }

    fun sendSoulmateGift(giftName: String, coinsCost: Long, intimacyGain: Int) {
        val success = repository.sendSoulmateGift(giftName, coinsCost, intimacyGain)
        if (success) {
            emitMessage("Sent $giftName to soulmate! +$intimacyGain intimacy 💕")
        } else {
            emitMessage("Insufficient coins to send $giftName!")
        }
    }

    fun submitReport(targetType: String, targetId: String, reason: String, details: String) {
        val ticket = repository.submitReport(targetType, targetId, reason, details)
        if (firestoreRepo != null && Firebase.auth.currentUser != null) {
            viewModelScope.launch {
                try {
                    firestoreRepo.submitReport(targetType, targetId, reason, details)
                } catch (e: Exception) {
                    Log.w("PartyViewModel", "Report submit error: ${e.message}")
                }
            }
        }
        emitMessage("Report submitted! Ticket ID: ${ticket.ticketId}. Clean room moderation will inspect promptly.")
        _showReportSheet.value = false
    }

    fun familyCheckIn() {
        val exp = repository.familyCheckIn()
        if (exp > 0) {
            emitMessage("Daily clan check-in complete! +$exp Family EXP & +1,000 Coins 🛡️")
        } else {
            emitMessage("Join a Family Guild first to check-in!")
        }
    }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _uiEvent.emit(msg)
        }
    }
}
