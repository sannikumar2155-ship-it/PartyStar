package com.example.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.WinGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class FruitItem(val emoji: String, val name: String, val multiplier3: Int, val multiplier2: Int)

val FRUIT_SYMBOLS = listOf(
    FruitItem("🍉", "Watermelon", 15, 3),
    FruitItem("🍇", "Grape", 10, 2),
    FruitItem("🍊", "Orange", 8, 2),
    FruitItem("🍒", "Cherry", 5, 2),
    FruitItem("🔔", "Bell", 25, 5),
    FruitItem("⭐", "Star", 50, 10),
    FruitItem("7️⃣", "Lucky 7", 100, 20)
)

@Composable
fun FruityPartyGame(
    user: UserProfile?,
    onClose: () -> Unit,
    onUpdateCoins: (Long) -> Boolean
) {
    var reel1 by remember { mutableStateOf(FRUIT_SYMBOLS[0]) }
    var reel2 by remember { mutableStateOf(FRUIT_SYMBOLS[1]) }
    var reel3 by remember { mutableStateOf(FRUIT_SYMBOLS[2]) }

    var isSpinning by remember { mutableStateOf(false) }
    var betAmount by remember { mutableStateOf(500L) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var lastWinCoins by remember { mutableStateOf(0L) }

    val scope = rememberCoroutineScope()
    val betPresets = listOf(100L, 500L, 1000L, 2500L)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF350B42), Color(0xFF160621))
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🍉 FRUITY PARTY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonPink
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonCyan.copy(alpha = 0.25f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("SLOT MACHINE", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
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
                    modifier = Modifier.testTag("close_fruity_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Slot Machine Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF220831))
                .border(2.dp, NeonPink, RoundedCornerShape(20.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReelBox(item = reel1, isSpinning = isSpinning)
                ReelBox(item = reel2, isSpinning = isSpinning)
                ReelBox(item = reel3, isSpinning = isSpinning)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Result Banner
        if (resultText != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (lastWinCoins > 0) WinGreen.copy(alpha = 0.25f) else Color.Red.copy(alpha = 0.2f))
                    .border(1.dp, if (lastWinCoins > 0) WinGreen else Color.Red, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = resultText ?: "",
                    color = if (lastWinCoins > 0) WinGreen else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Payout Paytable Hint
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("7️⃣7️⃣7️⃣ = 100x", fontSize = 11.sp, color = NeonGold, fontWeight = FontWeight.Bold)
            Text("⭐⭐⭐ = 50x", fontSize = 11.sp, color = NeonCyan)
            Text("🔔🔔🔔 = 25x", fontSize = 11.sp, color = NeonPink)
            Text("🍉🍉🍉 = 15x", fontSize = 11.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bet Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            betPresets.forEach { amount ->
                val isSelected = betAmount == amount
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonPink.copy(alpha = 0.3f) else Color(0xFF260F38))
                        .border(1.dp, if (isSelected) NeonPink else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable(enabled = !isSpinning) { betAmount = amount }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$amount",
                        color = if (isSelected) NeonPink else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Spin Button
        Button(
            onClick = {
                val currentCoins = user?.coins ?: 0L
                if (currentCoins < betAmount) {
                    resultText = "Insufficient coins! Please recharge."
                    lastWinCoins = 0
                    return@Button
                }

                if (!onUpdateCoins(-betAmount)) return@Button

                isSpinning = true
                resultText = null
                scope.launch {
                    repeat(14) {
                        reel1 = FRUIT_SYMBOLS.random()
                        reel2 = FRUIT_SYMBOLS.random()
                        reel3 = FRUIT_SYMBOLS.random()
                        delay(75)
                    }

                    // Compute result
                    val f1 = FRUIT_SYMBOLS.random()
                    val f2 = FRUIT_SYMBOLS.random()
                    val f3 = if (Math.random() < 0.25) f1 else FRUIT_SYMBOLS.random()

                    reel1 = f1
                    reel2 = f2
                    reel3 = f3

                    val payout: Long
                    if (f1.emoji == f2.emoji && f2.emoji == f3.emoji) {
                        payout = betAmount * f1.multiplier3
                        onUpdateCoins(payout)
                        lastWinCoins = payout
                        resultText = "🎉 JACKPOT! 3x ${f1.name} won $payout coins! (${f1.multiplier3}x)"
                    } else if (f1.emoji == f2.emoji || f2.emoji == f3.emoji || f1.emoji == f3.emoji) {
                        val matched = if (f1.emoji == f2.emoji) f1 else if (f2.emoji == f3.emoji) f2 else f1
                        payout = betAmount * matched.multiplier2
                        onUpdateCoins(payout)
                        lastWinCoins = payout
                        resultText = "✨ Match 2! Won $payout coins! (${matched.multiplier2}x)"
                    } else {
                        payout = 0L
                        lastWinCoins = 0L
                        resultText = "No match this spin! Keep rolling for the 777 Jackpot!"
                    }

                    isSpinning = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("fruity_spin_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
            shape = RoundedCornerShape(12.dp),
            enabled = !isSpinning
        ) {
            Text(
                text = if (isSpinning) "SPINNING REELS..." else "SPIN (Bet $betAmount 💰)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun ReelBox(item: FruitItem, isSpinning: Boolean) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF13041C))
            .border(2.dp, NeonPurple, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.emoji,
            fontSize = 38.sp
        )
    }
}
