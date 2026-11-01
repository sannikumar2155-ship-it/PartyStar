package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.WinGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PlayingCard(val rank: String, val suit: String, val value: Int)

fun createDeck(): List<PlayingCard> {
    val suits = listOf("♠️", "♥️", "♦️", "♣️")
    val ranks = listOf(
        "2" to 2, "3" to 3, "4" to 4, "5" to 5, "6" to 6,
        "7" to 7, "8" to 8, "9" to 9, "10" to 10,
        "J" to 11, "Q" to 12, "K" to 13, "A" to 14
    )
    val deck = mutableListOf<PlayingCard>()
    for (suit in suits) {
        for ((r, v) in ranks) {
            deck.add(PlayingCard(r, suit, v))
        }
    }
    return deck.shuffled()
}

// Hand evaluation score: higher is better
fun evaluateTeenPattiHand(cards: List<PlayingCard>): Pair<String, Int> {
    if (cards.size < 3) return "Unknown" to 0
    val sorted = cards.sortedByDescending { it.value }
    val v1 = sorted[0].value
    val v2 = sorted[1].value
    val v3 = sorted[2].value
    val isFlush = cards.all { it.suit == cards[0].suit }
    val isStraight = (v1 - v2 == 1 && v2 - v3 == 1) || (v1 == 14 && v2 == 3 && v3 == 2) // A-2-3

    // 1. Trail / Trio (Three of a kind)
    if (v1 == v2 && v2 == v3) {
        return "Trail (Trio) 👑" to (60000 + v1)
    }
    // 2. Pure Sequence (Straight Flush)
    if (isStraight && isFlush) {
        return "Pure Sequence 🔥" to (50000 + v1)
    }
    // 3. Sequence (Straight)
    if (isStraight) {
        return "Sequence ⚡" to (40000 + v1)
    }
    // 4. Color (Flush)
    if (isFlush) {
        return "Color (Flush) 🎨" to (30000 + v1)
    }
    // 5. Pair
    if (v1 == v2 || v2 == v3 || v1 == v3) {
        val pairVal = if (v1 == v2) v1 else v3
        return "Pair 👯" to (20000 + pairVal)
    }
    // 6. High Card
    return "High Card (${sorted[0].rank})" to (10000 + v1)
}

@Composable
fun TeenPattiGame(
    user: UserProfile?,
    onClose: () -> Unit,
    onUpdateCoins: (Long) -> Boolean
) {
    var hasSeenCards by remember { mutableStateOf(false) }
    var potCoins by remember { mutableLongStateOf(4000L) }
    var bootAmount by remember { mutableLongStateOf(500L) }
    var isShowdown by remember { mutableStateOf(false) }
    var showdownResult by remember { mutableStateOf<String?>(null) }
    var playerFolded by remember { mutableStateOf(false) }

    var playerCards by remember {
        mutableStateOf(
            listOf(
                PlayingCard("A", "♠️", 14),
                PlayingCard("K", "♠️", 13),
                PlayingCard("Q", "♠️", 12)
            )
        )
    }

    var dealerCards by remember {
        mutableStateOf(
            listOf(
                PlayingCard("J", "♥️", 11),
                PlayingCard("10", "♦️", 10),
                PlayingCard("9", "♣️", 9)
            )
        )
    }

    val scope = rememberCoroutineScope()

    fun startNewRound() {
        val deck = createDeck()
        playerCards = deck.subList(0, 3)
        dealerCards = deck.subList(3, 6)
        hasSeenCards = false
        isShowdown = false
        showdownResult = null
        playerFolded = false
        potCoins = bootAmount * 4
        onUpdateCoins(-bootAmount)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF042918), Color(0xFF02170D))
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🃏 TEEN PATTI",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonGold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(WinGreen.copy(alpha = 0.25f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("3-CARD ROYAL", fontSize = 10.sp, color = WinGreen, fontWeight = FontWeight.Bold)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "💰 ${user?.coins ?: 0}",
                    color = GoldCoin,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_teenpatti_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Table Arena
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF0A4D2E), Color(0xFF032213))
                    )
                )
                .border(2.dp, NeonGold, RoundedCornerShape(20.dp))
                .padding(12.dp)
        ) {
            // Dealer Area (Top)
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Dealer / Rival",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    dealerCards.forEach { card ->
                        CardView(card = card, isHidden = !isShowdown)
                    }
                }
            }

            // Central Pot Pool
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF02130A).copy(alpha = 0.85f))
                    .border(1.dp, NeonGold, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "POT: $potCoins 💰",
                    color = NeonGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }

            // Player Hand (Bottom)
            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    playerCards.forEach { card ->
                        CardView(card = card, isHidden = !hasSeenCards && !isShowdown)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                val (handName, _) = evaluateTeenPattiHand(playerCards)
                Text(
                    text = if (hasSeenCards || isShowdown) "Your Hand: $handName" else "Tap 'See Cards' or Play Blind",
                    fontSize = 11.sp,
                    color = if (hasSeenCards) NeonCyan else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Showdown outcome message
        if (showdownResult != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (showdownResult?.contains("WON") == true) WinGreen.copy(alpha = 0.25f) else DangerRed.copy(alpha = 0.2f))
                    .border(1.dp, if (showdownResult?.contains("WON") == true) WinGreen else DangerRed, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = showdownResult ?: "",
                    color = if (showdownResult?.contains("WON") == true) WinGreen else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Action Buttons Row
        if (!isShowdown && !playerFolded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // See Cards Toggle
                Button(
                    onClick = { hasSeenCards = !hasSeenCards },
                    modifier = Modifier.weight(1f).height(44.dp).testTag("teenpatti_see_cards_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = if (hasSeenCards) NeonPurple else Color(0xFF1B4332)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (hasSeenCards) "Seen 👀" else "See Cards 👁️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Chaal / Bet Call
                val chaalCost = if (hasSeenCards) bootAmount * 2 else bootAmount
                Button(
                    onClick = {
                        val userCoins = user?.coins ?: 0L
                        if (userCoins < chaalCost) return@Button
                        if (onUpdateCoins(-chaalCost)) {
                            potCoins += chaalCost * 2
                        }
                    },
                    modifier = Modifier.weight(1f).height(44.dp).testTag("teenpatti_chaal_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Chaal $chaalCost 💰", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                // Show (Showdown)
                Button(
                    onClick = {
                        val (pHand, pScore) = evaluateTeenPattiHand(playerCards)
                        val (dHand, dScore) = evaluateTeenPattiHand(dealerCards)
                        isShowdown = true
                        hasSeenCards = true

                        if (pScore >= dScore) {
                            onUpdateCoins(potCoins)
                            showdownResult = "🎉 YOU WON $potCoins COINS! ($pHand beat $dHand)"
                        } else {
                            showdownResult = "Dealer won with $dHand! You had $pHand."
                        }
                    },
                    modifier = Modifier.weight(1f).height(44.dp).testTag("teenpatti_show_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = WinGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Show 🏆", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                // Pack (Fold)
                Button(
                    onClick = {
                        playerFolded = true
                        showdownResult = "Folded! Better luck next hand."
                    },
                    modifier = Modifier.weight(1f).height(44.dp).testTag("teenpatti_pack_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Pack ❌", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // New Hand Button
            Button(
                onClick = { startNewRound() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("teenpatti_new_round_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "DEAL NEW HAND (Boot $bootAmount 💰)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun CardView(card: PlayingCard, isHidden: Boolean) {
    Box(
        modifier = Modifier
            .size(width = 38.dp, height = 52.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isHidden) Color(0xFF1E3A8A) else Color.White)
            .border(1.dp, if (isHidden) NeonGold else Color.DarkGray, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (isHidden) {
            Text("🂠", fontSize = 22.sp, color = NeonGold)
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val isRed = card.suit == "♥️" || card.suit == "♦️"
                Text(
                    text = card.rank,
                    color = if (isRed) Color(0xFFD32F2F) else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = card.suit,
                    fontSize = 12.sp
                )
            }
        }
    }
}
