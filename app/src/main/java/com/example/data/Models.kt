package com.example.data

enum class LoginMethod(val label: String) {
    PHONE("Mobile Number"),
    GMAIL("Google / Gmail"),
    EMAIL("Email / Password"),
    FACEBOOK("Facebook"),
    GUEST("Guest Account")
}

enum class RingTier(val title: String, val bonus: String, val multiplier: Float) {
    SILVER("Silver Promise Ring", "5% Room Exp Bonus", 1.05f),
    GOLD("Golden Eternity Ring", "15% Room Exp + CP Badge", 1.15f),
    DIAMOND("Diamond Solitaire Ring", "30% Exp + Couple Aura", 1.30f),
    CELESTIAL("Celestial Galaxy Ring", "50% Exp + Royal Entrance", 1.50f)
}

data class SoulmateInfo(
    val partnerName: String,
    val partnerAvatar: String,
    val ringTier: RingTier,
    val intimacyScore: Int,
    val daysTogether: Int,
    val confessionNote: String
)

data class FamilyMembership(
    val familyId: String,
    val familyName: String,
    val familyBadge: String,
    val role: String,
    val familyLevel: Int,
    val weeklyExp: Int
)

data class UserProfile(
    val id: String,
    val name: String,
    val avatar: String,
    val loginMethod: LoginMethod,
    val phone: String? = null,
    val email: String? = null,
    val coins: Long = 50000,
    val diamonds: Long = 1200,
    val vipLevel: Int = 2,
    val soulmate: SoulmateInfo? = null,
    val family: FamilyMembership? = null
)

data class MicSeat(
    val index: Int,
    val userId: String? = null,
    val userName: String? = null,
    val userAvatar: String? = null,
    val isMuted: Boolean = false,
    val isTalking: Boolean = false,
    val hasSoulmateRing: Boolean = false
)

data class PartyRoomData(
    val id: String,
    val title: String,
    val hostName: String,
    val hostAvatar: String,
    val tag: String,
    val popularity: Int,
    val isFamilyRoom: Boolean = false,
    val announcement: String,
    val seats: List<MicSeat> = emptyList()
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val text: String,
    val isSystem: Boolean = false,
    val isGift: Boolean = false,
    val giftEmoji: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class FloatingEmoji(
    val id: Long,
    val emoji: String,
    val startXFraction: Float
)

enum class GameType(val displayName: String, val subtitle: String, val icon: String) {
    LUCKY_77("Lucky 77", "Dice & 7 Predictor (Up to 25x)", "🎲"),
    FRUITY_PARTY("Fruity Party", "Juicy Slots & Wild Multipliers", "🍉"),
    ORIGINAL_777("Original 777", "Classic Vegas Triple Reel", "🎰"),
    ROCKET_LAUNCH("Rocket Launch", "Crash Multiplier - Cash Out Fast!", "🚀"),
    TEEN_PATTI("Teen Patti", "Classic 3-Card Table with Chaal & Show", "🃏")
}

data class RechargePack(
    val id: String,
    val amountInr: Int,
    val coins: Long,
    val bonusCoins: Long,
    val tag: String? = null
)

enum class PaymentCategory {
    UPI,
    WALLET,
    NETBANKING
}

data class PaymentOption(
    val id: String,
    val name: String,
    val category: PaymentCategory,
    val iconEmoji: String,
    val subtitle: String
)

data class RechargeReceipt(
    val txnId: String,
    val amountInr: Int,
    val coinsAdded: Long,
    val method: String,
    val timestamp: Long,
    val status: String = "SUCCESS"
)

data class ReportTicket(
    val ticketId: String,
    val targetType: String,
    val targetId: String,
    val reason: String,
    val details: String,
    val status: String = "Under Review",
    val timestamp: Long = System.currentTimeMillis()
)

data class FeedbackItem(
    val id: String,
    val category: String,
    val feedback: String,
    val rating: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class FamilyRoomData(
    val id: String,
    val name: String,
    val motto: String,
    val level: Int,
    val members: Int,
    val totalExp: Long,
    val chestProgress: Float,
    val chestLevel: Int,
    val leaderName: String
)
