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
import com.example.ui.theme.WinGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SlotSymbol(val id: String, val display: String, val multiplier3: Int, val isSeven: Boolean = false)

val VEGAS_777_SYMBOLS = listOf(
    SlotSymbol("triple_7", "7️⃣7️⃣7️⃣", 200, isSeven = true),
    SlotSymbol("double_7", "7️⃣7️⃣", 75, isSeven = true),
    SlotSymbol("single_7", "7️⃣", 25, isSeven = true),
    SlotSymbol("bar_3", "🍫🍫", 15),
    SlotSymbol("diamond", "💎", 40),
    SlotSymbol("bell", "🔔", 10),
    SlotSymbol("cherry", "🍒", 5)
)

@Composable
fun Original777Game(
    user: UserProfile?,
    onClose: () -> Unit,
    onUpdateCoins: (Long) -> Boolean
) {
    var reelA by remember { mutableStateOf(VEGAS_777_SYMBOLS[0]) }
    var reelB by remember { mutableStateOf(VEGAS_777_SYMBOLS[1]) }
    var reelC by remember { mutableStateOf(VEGAS_777_SYMBOLS[2]) }

    var isPulling by remember { mutableStateOf(false) }
    var betAmount by remember { mutableStateOf(1000L) }
    var payoutMsg by remember { mutableStateOf<String?>(null) }
    var wonCoins by remember { mutableStateOf(0L) }

    val scope = rememberCoroutineScope()
    val betPresets = listOf(250L, 500L, 1000L, 5000L)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF261202), Color(0xFF140801), Color(0xFF0D0308))
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Vegas Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🎰 ORIGINAL 777",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonGold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonGold.copy(alpha = 0.25f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("VEGAS CLASSIC", fontSize = 10.sp, color = NeonGold, fontWeight = FontWeight.Bold)
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
                    modifier = Modifier.testTag("close_original777_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Vegas Slot Machine Body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF4A2500), Color(0xFF241000))
                    )
                )
                .border(3.dp, NeonGold, RoundedCornerShape(20.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Jackpot Ribbon
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF8B0000))
                        .border(1.dp, NeonGold, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "GRAND JACKPOT 777: 200X",
                        color = NeonGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // The 3 Reels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VegasReel(reelA)
                    VegasReel(reelB)
                    VegasReel(reelC)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Win result banner
        if (payoutMsg != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (wonCoins > 0) WinGreen.copy(alpha = 0.25f) else Color.Red.copy(alpha = 0.2f))
                    .border(1.dp, if (wonCoins > 0) WinGreen else Color.Red, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = payoutMsg ?: "",
                    color = if (wonCoins > 0) WinGreen else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Payout Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("777 = 200x", fontSize = 11.sp, color = NeonGold, fontWeight = FontWeight.Bold)
            Text("💎 = 40x", fontSize = 11.sp, color = NeonCyan)
            Text("77 = 75x", fontSize = 11.sp, color = NeonGold)
            Text("BAR = 15x", fontSize = 11.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bet Presets
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
                        .background(if (isSelected) NeonGold.copy(alpha = 0.35f) else Color(0xFF3B1E05))
                        .border(1.dp, if (isSelected) NeonGold else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable(enabled = !isPulling) { betAmount = amount }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$amount",
                        color = if (isSelected) NeonGold else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pull Lever / Spin Button
        Button(
            onClick = {
                val currentCoins = user?.coins ?: 0L
                if (currentCoins < betAmount) {
                    payoutMsg = "Insufficient coins! Please recharge."
                    wonCoins = 0
                    return@Button
                }

                if (!onUpdateCoins(-betAmount)) return@Button

                isPulling = true
                payoutMsg = null
                scope.launch {
                    repeat(16) {
                        reelA = VEGAS_777_SYMBOLS.random()
                        reelB = VEGAS_777_SYMBOLS.random()
                        reelC = VEGAS_777_SYMBOLS.random()
                        delay(65)
                    }

                    val rand = Math.random()
                    val symA: SlotSymbol
                    val symB: SlotSymbol
                    val symC: SlotSymbol

                    if (rand < 0.12) {
                        // Jackpot 777!
                        val hit = VEGAS_777_SYMBOLS[0]
                        symA = hit
                        symB = hit
                        symC = hit
                    } else if (rand < 0.35) {
                        // Match 3 random
                        val chosen = VEGAS_777_SYMBOLS.random()
                        symA = chosen
                        symB = chosen
                        symC = chosen
                    } else if (rand < 0.60) {
                        // Match 2 sevens
                        symA = VEGAS_777_SYMBOLS.filter { it.isSeven }.random()
                        symB = symA
                        symC = VEGAS_777_SYMBOLS.random()
                    } else {
                        symA = VEGAS_777_SYMBOLS.random()
                        symB = VEGAS_777_SYMBOLS.random()
                        symC = VEGAS_777_SYMBOLS.random()
                    }

                    reelA = symA
                    reelB = symB
                    reelC = symC

                    if (symA.id == symB.id && symB.id == symC.id) {
                        val win = betAmount * symA.multiplier3
                        onUpdateCoins(win)
                        wonCoins = win
                        payoutMsg = "🔥 MEGA 777 HIT! 3x ${symA.display} won $win coins! (${symA.multiplier3}x)"
                    } else if (symA.isSeven && symB.isSeven && symC.isSeven) {
                        val win = betAmount * 30
                        onUpdateCoins(win)
                        wonCoins = win
                        payoutMsg = "✨ MIXED 7s WIN! Won $win coins! (30x)"
                    } else if (symA.id == symB.id || symB.id == symC.id) {
                        val win = betAmount * 3
                        onUpdateCoins(win)
                        wonCoins = win
                        payoutMsg = "Good spin! Pair matched! Won $win coins."
                    } else {
                        wonCoins = 0
                        payoutMsg = "Try again! Hit Triple 7 for 200x!"
                    }

                    isPulling = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("original777_spin_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
            shape = RoundedCornerShape(12.dp),
            enabled = !isPulling
        ) {
            Text(
                text = if (isPulling) "SPINNING 777..." else "PULL LEVER (Bet $betAmount 💰)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun VegasReel(sym: SlotSymbol) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF100600))
            .border(2.dp, NeonGold, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = sym.display,
            fontSize = if (sym.display.length > 2) 20.sp else 34.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
