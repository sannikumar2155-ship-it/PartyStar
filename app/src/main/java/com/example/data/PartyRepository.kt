package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class PartyRepository {

    private val defaultUser = UserProfile(
        id = "USER_98721",
        name = "RockStar_Rohan",
        avatar = "👑",
        loginMethod = LoginMethod.PHONE,
        phone = "+91 98765 43210",
        email = "rohan.party@gmail.com",
        coins = 75000L,
        diamonds = 1500L,
        vipLevel = 3,
        soulmate = SoulmateInfo(
            partnerName = "Princess_Simran",
            partnerAvatar = "💖",
            ringTier = RingTier.DIAMOND,
            intimacyScore = 8420,
            daysTogether = 142,
            confessionNote = "Forever together in our favorite party room! ✨"
        ),
        family = FamilyMembership(
            familyId = "FAM_ROYAL",
            familyName = "Royal Warriors",
            familyBadge = "⚔️",
            role = "Elder",
            familyLevel = 6,
            weeklyExp = 28400
        )
    )

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _rooms = MutableStateFlow<List<PartyRoomData>>(getInitialRooms())
    val rooms: StateFlow<List<PartyRoomData>> = _rooms.asStateFlow()

    private val _familyRooms = MutableStateFlow<List<FamilyRoomData>>(getInitialFamilyRooms())
    val familyRooms: StateFlow<List<FamilyRoomData>> = _familyRooms.asStateFlow()

    private val _activeRoom = MutableStateFlow<PartyRoomData?>(null)
    val activeRoom: StateFlow<PartyRoomData?> = _activeRoom.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(getInitialChat())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _floatingEmojis = MutableStateFlow<List<FloatingEmoji>>(emptyList())
    val floatingEmojis: StateFlow<List<FloatingEmoji>> = _floatingEmojis.asStateFlow()

    private val _reports = MutableStateFlow<List<ReportTicket>>(getInitialReports())
    val reports: StateFlow<List<ReportTicket>> = _reports.asStateFlow()

    private val _transactions = MutableStateFlow<List<RechargeReceipt>>(getInitialTransactions())
    val transactions: StateFlow<List<RechargeReceipt>> = _transactions.asStateFlow()

    // Recharge packs definition
    val rechargePacks = listOf(
        RechargePack("pack_50", 50, 5000L, 250L, "Starter Deal"),
        RechargePack("pack_100", 100, 11000L, 1000L, "Most Popular 🔥"),
        RechargePack("pack_250", 250, 30000L, 4000L, "Hot Value"),
        RechargePack("pack_500", 500, 65000L, 15000L, "+15% Extra!"),
        RechargePack("pack_1000", 1000, 150000L, 45000L, "VIP Gold Deal 👑"),
        RechargePack("pack_2000", 2000, 350000L, 120000L, "Mega Whale 💎")
    )

    val paymentOptions = listOf(
        PaymentOption("gpay", "Google Pay UPI", PaymentCategory.UPI, "🟢", "Instant via GPay UPI ID"),
        PaymentOption("phonepe", "PhonePe UPI", PaymentCategory.UPI, "🟣", "Instant via PhonePe QR/UPI"),
        PaymentOption("paytm_upi", "Paytm UPI", PaymentCategory.UPI, "🔵", "Zero convenience fee"),
        PaymentOption("bhim", "BHIM / Any UPI ID", PaymentCategory.UPI, "🇮🇳", "Direct VPA transfer"),
        PaymentOption("paytm_wallet", "Paytm Wallet", PaymentCategory.WALLET, "💳", "One-click instant deduction"),
        PaymentOption("phonepe_wallet", "PhonePe Wallet", PaymentCategory.WALLET, "👛", "Linked balance & cashback")
    )

    fun login(method: LoginMethod, identifier: String, name: String) {
        val user = UserProfile(
            id = "USER_${UUID.randomUUID().toString().take(6).uppercase()}",
            name = name.ifBlank { "StarUser_${(1000..9999).random()}" },
            avatar = listOf("😎", "🥳", "🔥", "👑", "🌟", "🦁").random(),
            loginMethod = method,
            phone = if (method == LoginMethod.PHONE) identifier else null,
            email = if (method == LoginMethod.GMAIL || method == LoginMethod.EMAIL) identifier else null,
            coins = 50000L,
            diamonds = 1000L,
            vipLevel = 1,
            soulmate = null,
            family = FamilyMembership(
                familyId = "FAM_ROYAL",
                familyName = "Royal Warriors",
                familyBadge = "⚔️",
                role = "Member",
                familyLevel = 6,
                weeklyExp = 5000
            )
        )
        _currentUser.value = user
    }

    fun setCurrentUser(user: UserProfile) {
        _currentUser.value = user
    }

    fun setRooms(roomList: List<PartyRoomData>) {
        if (roomList.isNotEmpty()) {
            _rooms.value = roomList
        }
    }

    fun setChatMessages(msgs: List<ChatMessage>) {
        _chatMessages.value = msgs
    }

    fun logout() {
        _currentUser.value = null
        _activeRoom.value = null
    }

    fun joinRoom(room: PartyRoomData) {
        _activeRoom.value = room
        // Add welcome message
        val welcomeMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = "SYSTEM",
            senderName = "Room System",
            senderAvatar = "📢",
            text = "Welcome to ${room.title}! Keep the chat clean and respectful. Enjoy the mini-games & voice party! 🌟",
            isSystem = true
        )
        _chatMessages.value = _chatMessages.value + welcomeMsg
    }

    fun leaveRoom() {
        _activeRoom.value = null
    }

    fun occupyMicSeat(seatIndex: Int) {
        val user = _currentUser.value ?: return
        val current = _activeRoom.value ?: return
        val updatedSeats = current.seats.map { seat ->
            if (seat.index == seatIndex) {
                seat.copy(
                    userId = user.id,
                    userName = user.name,
                    userAvatar = user.avatar,
                    isMuted = false,
                    isTalking = false,
                    hasSoulmateRing = user.soulmate != null
                )
            } else if (seat.userId == user.id) {
                // vacating old seat
                seat.copy(userId = null, userName = null, userAvatar = null, isTalking = false)
            } else seat
        }
        _activeRoom.value = current.copy(seats = updatedSeats)
    }

    fun toggleMicMute() {
        val user = _currentUser.value ?: return
        val current = _activeRoom.value ?: return
        val updatedSeats = current.seats.map { seat ->
            if (seat.userId == user.id) {
                seat.copy(isMuted = !seat.isMuted)
            } else seat
        }
        _activeRoom.value = current.copy(seats = updatedSeats)
    }

    fun toggleTalking(talking: Boolean) {
        val user = _currentUser.value ?: return
        val current = _activeRoom.value ?: return
        val updatedSeats = current.seats.map { seat ->
            if (seat.userId == user.id) {
                seat.copy(isTalking = if (seat.isMuted) false else talking)
            } else seat
        }
        _activeRoom.value = current.copy(seats = updatedSeats)
    }

    fun leaveMicSeat() {
        val user = _currentUser.value ?: return
        val current = _activeRoom.value ?: return
        val updatedSeats = current.seats.map { seat ->
            if (seat.userId == user.id) {
                seat.copy(userId = null, userName = null, userAvatar = null, isTalking = false)
            } else seat
        }
        _activeRoom.value = current.copy(seats = updatedSeats)
    }

    fun sendChatMessage(text: String) {
        val user = _currentUser.value ?: return
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = user.id,
            senderName = user.name,
            senderAvatar = user.avatar,
            text = text
        )
        _chatMessages.value = _chatMessages.value + msg
    }

    fun triggerFloatingEmoji(emoji: String) {
        val randomX = (15..85).random() / 100f
        val newEmoji = FloatingEmoji(
            id = System.currentTimeMillis() + (0..999).random(),
            emoji = emoji,
            startXFraction = randomX
        )
        val current = _floatingEmojis.value.takeLast(12)
        _floatingEmojis.value = current + newEmoji
    }

    fun updateCoins(delta: Long): Boolean {
        val user = _currentUser.value ?: return false
        val newBalance = user.coins + delta
        if (newBalance < 0) return false
        _currentUser.value = user.copy(coins = newBalance)
        return true
    }

    fun processRecharge(pack: RechargePack, method: PaymentOption): RechargeReceipt {
        val user = _currentUser.value ?: throw IllegalStateException("Not logged in")
        val totalCoinsAdded = pack.coins + pack.bonusCoins
        _currentUser.value = user.copy(coins = user.coins + totalCoinsAdded)

        val receipt = RechargeReceipt(
            txnId = "TXN_UPI_${System.currentTimeMillis().toString().takeLast(8)}",
            amountInr = pack.amountInr,
            coinsAdded = totalCoinsAdded,
            method = method.name,
            timestamp = System.currentTimeMillis(),
            status = "SUCCESS"
        )
        _transactions.value = listOf(receipt) + _transactions.value
        return receipt
    }

    fun upgradeSoulmateRing(tier: RingTier, partnerName: String): Boolean {
        val user = _currentUser.value ?: return false
        val cost = when (tier) {
            RingTier.SILVER -> 5000L
            RingTier.GOLD -> 15000L
            RingTier.DIAMOND -> 30000L
            RingTier.CELESTIAL -> 75000L
        }
        if (user.coins < cost) return false
        _currentUser.value = user.copy(
            coins = user.coins - cost,
            soulmate = SoulmateInfo(
                partnerName = partnerName.ifBlank { "My Soulmate" },
                partnerAvatar = "💖",
                ringTier = tier,
                intimacyScore = (user.soulmate?.intimacyScore ?: 0) + 1200,
                daysTogether = (user.soulmate?.daysTogether ?: 0) + 1,
                confessionNote = "Eternal love bound by the sacred ${tier.title} 💍"
            )
        )
        return true
    }

    fun sendSoulmateGift(giftName: String, coinsCost: Long, intimacyGain: Int): Boolean {
        val user = _currentUser.value ?: return false
        val sm = user.soulmate ?: return false
        if (user.coins < coinsCost) return false
        _currentUser.value = user.copy(
            coins = user.coins - coinsCost,
            soulmate = sm.copy(intimacyScore = sm.intimacyScore + intimacyGain)
        )
        // Add to room chat
        val giftMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = user.id,
            senderName = user.name,
            senderAvatar = user.avatar,
            text = "Sent a romantic $giftName to soulmate ${sm.partnerName}! (+${intimacyGain} Intimacy) 💍💕",
            isGift = true,
            giftEmoji = "💍"
        )
        _chatMessages.value = _chatMessages.value + giftMsg
        triggerFloatingEmoji("💖")
        triggerFloatingEmoji("💍")
        return true
    }

    fun submitReport(targetType: String, targetId: String, reason: String, details: String): ReportTicket {
        val ticket = ReportTicket(
            ticketId = "REP-${(100000..999999).random()}",
            targetType = targetType,
            targetId = targetId,
            reason = reason,
            details = details,
            status = "Under Review",
            timestamp = System.currentTimeMillis()
        )
        _reports.value = listOf(ticket) + _reports.value
        return ticket
    }

    fun familyCheckIn(): Int {
        val user = _currentUser.value ?: return 0
        val fam = user.family ?: return 0
        val expGain = 500
        val bonusCoins = 1000L
        _currentUser.value = user.copy(
            coins = user.coins + bonusCoins,
            family = fam.copy(weeklyExp = fam.weeklyExp + expGain)
        )
        return expGain
    }

    private fun getInitialRooms(): List<PartyRoomData> {
        return listOf(
            PartyRoomData(
                id = "ROOM_101",
                title = "🔥 Bollywood Night & Antakshari",
                hostName = "RJ_Aarav",
                hostAvatar = "🎙️",
                tag = "🎉 Music Party",
                popularity = 12480,
                isFamilyRoom = false,
                announcement = "Join mic for singing, banter & lucky dice rolls! Don't forget to send roses!",
                seats = (1..8).map { i ->
                    when (i) {
                        1 -> MicSeat(1, "USER_01", "RJ_Aarav", "🎙️", isMuted = false, isTalking = true)
                        2 -> MicSeat(2, "USER_02", "Pooja_Sing", "🌸", isMuted = false, isTalking = false, hasSoulmateRing = true)
                        3 -> MicSeat(3, "USER_03", "Kabir_DJ", "🎧", isMuted = false, isTalking = false)
                        else -> MicSeat(i)
                    }
                }
            ),
            PartyRoomData(
                id = "ROOM_102",
                title = "❤️ Soulmate & CP Confession Hub",
                hostName = "Queen_Alia",
                hostAvatar = "👑",
                tag = "❤️ CP Love Hub",
                popularity = 8920,
                isFamilyRoom = false,
                announcement = "Wear your Soulmate Ring and level up your CP intimacy score today! ✨",
                seats = (1..8).map { i ->
                    when (i) {
                        1 -> MicSeat(1, "USER_04", "Queen_Alia", "👑", isMuted = false, isTalking = true, hasSoulmateRing = true)
                        2 -> MicSeat(2, "USER_05", "King_Farhan", "🤴", isMuted = false, isTalking = false, hasSoulmateRing = true)
                        else -> MicSeat(i)
                    }
                }
            ),
            PartyRoomData(
                id = "ROOM_103",
                title = "🎰 777 & Rocket High-Rollers Lounge",
                hostName = "LuckyStrike",
                hostAvatar = "🎲",
                tag = "💎 High Rollers",
                popularity = 19500,
                isFamilyRoom = false,
                announcement = "Rocket multiplier hit 48.5x earlier! Spin Lucky 77 and Fruity Party now!",
                seats = (1..8).map { i ->
                    when (i) {
                        1 -> MicSeat(1, "USER_06", "LuckyStrike", "🎲", isMuted = false, isTalking = true)
                        2 -> MicSeat(2, "USER_07", "CryptoSam", "🚀", isMuted = false, isTalking = false)
                        3 -> MicSeat(3, "USER_08", "VegasRani", "💎", isMuted = false, isTalking = false)
                        4 -> MicSeat(4, "USER_09", "PattiMaster", "🃏", isMuted = false, isTalking = false)
                        else -> MicSeat(i)
                    }
                }
            ),
            PartyRoomData(
                id = "ROOM_104",
                title = "⚔️ Royal Warriors Clan Base",
                hostName = "Commander_Vikram",
                hostAvatar = "🛡️",
                tag = "⚔️ Family Guild",
                popularity = 15300,
                isFamilyRoom = true,
                announcement = "Clan war starts tonight at 9 PM! Open daily Family Treasure chest for free coins!",
                seats = (1..8).map { i ->
                    when (i) {
                        1 -> MicSeat(1, "USER_10", "Commander_Vikram", "🛡️", isMuted = false, isTalking = true)
                        2 -> MicSeat(2, "USER_11", "Tiger_Shroff", "🐯", isMuted = false, isTalking = false)
                        else -> MicSeat(i)
                    }
                }
            )
        )
    }

    private fun getInitialFamilyRooms(): List<FamilyRoomData> {
        return listOf(
            FamilyRoomData(
                id = "FAM_ROYAL",
                name = "Royal Warriors",
                motto = "United we conquer every party leaderboard!",
                level = 6,
                members = 48,
                totalExp = 385000L,
                chestProgress = 0.78f,
                chestLevel = 4,
                leaderName = "Commander_Vikram"
            ),
            FamilyRoomData(
                id = "FAM_PHOENIX",
                name = "Phoenix Empire",
                motto = "Rising together with music & high spirits 🔥",
                level = 5,
                members = 42,
                totalExp = 290000L,
                chestProgress = 0.52f,
                chestLevel = 3,
                leaderName = "Phoenix_Lord"
            ),
            FamilyRoomData(
                id = "FAM_HEARTS",
                name = "Sweet Harmony",
                motto = "The premier couple & soulmate family guild 💖",
                level = 4,
                members = 36,
                totalExp = 210000L,
                chestProgress = 0.90f,
                chestLevel = 3,
                leaderName = "Harmony_Queen"
            )
        )
    }

    private fun getInitialChat(): List<ChatMessage> {
        return listOf(
            ChatMessage("1", "USER_01", "RJ_Aarav", "🎙️", "Welcome everyone! Playing the latest tracks right now 🎶"),
            ChatMessage("2", "USER_02", "Pooja_Sing", "🌸", "Hi RJ! Can you play some romantic songs? 💕"),
            ChatMessage("3", "USER_07", "CryptoSam", "🚀", "Rocket Launch just hit 22.4x! Cashed out 50k coins!! 🚀🔥"),
            ChatMessage("4", "USER_08", "VegasRani", "💎", "Anyone up for Lucky 77 or Teen Patti tables?"),
            ChatMessage("5", "SYSTEM", "PartyStar Bot", "🤖", "Pooja_Sing sent 99 Roses to RJ_Aarav! 🌹✨", isGift = true, giftEmoji = "🌹")
        )
    }

    private fun getInitialReports(): List<ReportTicket> {
        return listOf(
            ReportTicket(
                ticketId = "REP-392180",
                targetType = "User",
                targetId = "SpamBot_42",
                reason = "Spam messages in room chat",
                details = "User was posting repetitive links.",
                status = "Resolved",
                timestamp = System.currentTimeMillis() - 86400000
            )
        )
    }

    private fun getInitialTransactions(): List<RechargeReceipt> {
        return listOf(
            RechargeReceipt(
                txnId = "TXN_UPI_98321041",
                amountInr = 100,
                coinsAdded = 12000L,
                method = "Google Pay UPI",
                timestamp = System.currentTimeMillis() - 172800000,
                status = "SUCCESS"
            )
        )
    }
}
