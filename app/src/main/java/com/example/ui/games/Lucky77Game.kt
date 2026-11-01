package com.example.ui.games

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.example.ui.theme.PartyBg
import com.example.ui.theme.PartyCard
import com.example.ui.theme.WinGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Lucky77Bet(val label: String, val multiplier: Float, val rangeDesc: String) {
    UNDER_7("Under 7", 2.1f, "Sum 2 to 6"),
    EXACT_7("Lucky 7", 5.8f, "Exact Sum 7"),
    OVER_7("Over 7", 2.1f, "Sum 8 to 12"),
    JACKPOT_77("Double 77", 25.0f, "Dice Twin 7s")
}

@Composable
fun Lucky77Game(
    user: UserProfile?,
    onClose: () -> Unit,
    onUpdateCoins: (Long) -> Boolean
) {
    var selectedBetType by remember { mutableStateOf(Lucky77Bet.EXACT_7) }
    var betAmount by remember { mutableStateOf(500L) }
    var isRolling by remember { mutableStateOf(false) }

    var die1 by remember { mutableIntStateOf(3) }
    var die2 by remember { mutableIntStateOf(4) }
    var lastWin by remember { mutableStateOf<String?>(null) }
    var winCoins by remember { mutableStateOf(0L) }

    val diceRotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val betPresets = listOf(100L, 500L, 1000L, 5000L)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2B0A4E), Color(0xFF140526))
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
                    text = "🎲 LUCKY 77",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonGold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonPink.copy(alpha = 0.3f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("LIVE TABLE", fontSize = 10.sp, color = NeonPink, fontWeight = FontWeight.Bold)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "💰 ${user?.coins ?: 0}",
                    color = GoldCoin,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_lucky77_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dice Roll Arena
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1B0733))
                .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DieBox(value = die1, rotation = diceRotation.value)
                    Spacer(modifier = Modifier.width(24.dp))
                    DieBox(value = die2, rotation = -diceRotation.value)
                }

                Spacer(modifier = Modifier.height(8.dp))

                val sum = die1 + die2
                Text(
                    text = if (isRolling) "Rolling..." else "Total Sum = $sum ${if (sum == 7) "🎉 LUCKY 7!" else ""}",
                    color = if (sum == 7) NeonGold else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        if (lastWin != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (winCoins > 0) WinGreen.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f))
                    .border(
                        1.dp,
                        if (winCoins > 0) WinGreen else Color.Red,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lastWin ?: "",
                    color = if (winCoins > 0) WinGreen else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bet Options Row
        Text(
            text = "Select Prediction:",
            fontSize = 12.sp,
            color = Color.LightGray,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Lucky77Bet.values().forEach { bet ->
                val isSelected = selectedBetType == bet
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) Brush.verticalGradient(listOf(NeonPink, NeonPurple))
                            else Brush.verticalGradient(listOf(PartyCard, PartyBg))
                        )
                        .border(
                            1.dp,
                            if (isSelected) NeonGold else Color.DarkGray,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = !isRolling) { selectedBetType = bet }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = bet.label,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${bet.multiplier}x",
                            color = if (isSelected) NeonGold else NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bet Preset Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            betPresets.forEach { amount ->
                val isSelected = betAmount == amount
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonGold.copy(alpha = 0.25f) else Color(0xFF24103E))
                        .border(1.dp, if (isSelected) NeonGold else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable(enabled = !isRolling) { betAmount = amount }
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

        // Roll Button
        Button(
            onClick = {
                val currentCoins = user?.coins ?: 0L
                if (currentCoins < betAmount) {
                    lastWin = "Not enough coins! Please recharge."
                    winCoins = 0
                    return@Button
                }

                if (!onUpdateCoins(-betAmount)) return@Button

                isRolling = true
                lastWin = null
                scope.launch {
                    diceRotation.animateTo(
                        targetValue = diceRotation.value + 720f,
                        animationSpec = tween(700, easing = FastOutSlowInEasing)
                    )
                    repeat(8) {
                        die1 = (1..6).random()
                        die2 = (1..6).random()
                        delay(60)
                    }
                    val finalD1 = (1..6).random()
                    val finalD2 = (1..6).random()
                    die1 = finalD1
                    die2 = finalD2
                    val sum = finalD1 + finalD2

                    val isWon = when (selectedBetType) {
                        Lucky77Bet.UNDER_7 -> sum in 2..6
                        Lucky77Bet.EXACT_7 -> sum == 7
                        Lucky77Bet.OVER_7 -> sum in 8..12
                        Lucky77Bet.JACKPOT_77 -> (finalD1 == 6 && finalD2 == 6) || (sum == 7 && finalD1 == finalD2)
                    }

                    if (isWon) {
                        val payout = (betAmount * selectedBetType.multiplier).toLong()
                        onUpdateCoins(payout)
                        winCoins = payout
                        lastWin = "🎉 WON ${payout} COINS! (${selectedBetType.multiplier}x)"
                    } else {
                        winCoins = 0
                        lastWin = "Missed! Rolled $sum. Try next round!"
                    }
                    isRolling = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("lucky77_roll_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
            shape = RoundedCornerShape(12.dp),
            enabled = !isRolling
        ) {
            Text(
                text = if (isRolling) "ROLLING DICE..." else "ROLL NOW (Bet $betAmount 💰)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun DieBox(value: Int, rotation: Float) {
    val dots = when (value) {
        1 -> listOf(Alignment.Center)
        2 -> listOf(Alignment.TopStart, Alignment.BottomEnd)
        3 -> listOf(Alignment.TopStart, Alignment.Center, Alignment.BottomEnd)
        4 -> listOf(Alignment.TopStart, Alignment.TopEnd, Alignment.BottomStart, Alignment.BottomEnd)
        5 -> listOf(Alignment.TopStart, Alignment.TopEnd, Alignment.Center, Alignment.BottomStart, Alignment.BottomEnd)
        else -> listOf(
            Alignment.TopStart, Alignment.TopEnd,
            Alignment.CenterStart, Alignment.CenterEnd,
            Alignment.BottomStart, Alignment.BottomEnd
        )
    }

    Box(
        modifier = Modifier
            .size(56.dp)
            .rotate(rotation)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(2.dp, NeonGold, RoundedCornerShape(12.dp))
            .padding(6.dp)
    ) {
        dots.forEach { alignment ->
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE91E63))
                    .align(alignment)
            )
        }
    }
}
