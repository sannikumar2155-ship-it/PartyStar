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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.WinGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class RocketGameState {
    IDLE,
    FLYING,
    CRASHED,
    CASHED_OUT
}

@Composable
fun RocketLaunchGame(
    user: UserProfile?,
    onClose: () -> Unit,
    onUpdateCoins: (Long) -> Boolean
) {
    var gameState by remember { mutableStateOf(RocketGameState.IDLE) }
    var currentMultiplier by remember { mutableFloatStateOf(1.00f) }
    var betAmount by remember { mutableStateOf(500L) }
    var cashedOutWin by remember { mutableStateOf(0L) }
    var cashOutMultiplier by remember { mutableFloatStateOf(1.0f) }

    val recentCrashes = remember {
        mutableStateListOf(2.15f, 1.42f, 5.80f, 1.12f, 12.4f, 3.20f)
    }

    val rocketY = remember { Animatable(0f) }
    val rocketX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val betPresets = listOf(100L, 500L, 1000L, 2500L)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D0B2E), Color(0xFF050317))
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
                    text = "🚀 ROCKET LAUNCH",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonPink.copy(alpha = 0.25f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("CRASH MULTIPLIER", fontSize = 10.sp, color = NeonPink, fontWeight = FontWeight.Bold)
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
                    modifier = Modifier.testTag("close_rocket_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Recent Multipliers Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("History:", fontSize = 10.sp, color = Color.Gray)
            recentCrashes.takeLast(5).forEach { m ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (m >= 2.0f) WinGreen.copy(alpha = 0.2f) else DangerRed.copy(alpha = 0.2f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "%.2fx".format(m),
                        color = if (m >= 2.0f) WinGreen else DangerRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Flight Canvas Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF1E1547), Color(0xFF09061C))
                    )
                )
                .border(1.5.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Altitude stars / grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = when (gameState) {
                        RocketGameState.IDLE -> "PREPARE FOR LAUNCH"
                        RocketGameState.FLYING -> "ROCKET IN FLIGHT!"
                        RocketGameState.CRASHED -> "💥 EXPLODED AT %.2fx".format(currentMultiplier)
                        RocketGameState.CASHED_OUT -> "🎉 CASHED OUT AT %.2fx!".format(cashOutMultiplier)
                    },
                    color = when (gameState) {
                        RocketGameState.FLYING -> NeonCyan
                        RocketGameState.CRASHED -> DangerRed
                        RocketGameState.CASHED_OUT -> WinGreen
                        else -> Color.LightGray
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "%.2fx".format(currentMultiplier),
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = when (gameState) {
                        RocketGameState.FLYING -> NeonGold
                        RocketGameState.CRASHED -> DangerRed
                        RocketGameState.CASHED_OUT -> WinGreen
                        else -> Color.White
                    }
                )
            }

            // Rocket Avatar
            val offsetX = rocketX.value.dp
            val offsetY = rocketY.value.dp
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToPx(), offsetY.roundToPx()) }
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = if (gameState == RocketGameState.CRASHED) "💥" else "🚀",
                    fontSize = 36.sp
                )
            }
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
                        .background(if (isSelected) NeonCyan.copy(alpha = 0.3f) else Color(0xFF1A153A))
                        .border(1.dp, if (isSelected) NeonCyan else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable(enabled = gameState == RocketGameState.IDLE || gameState == RocketGameState.CRASHED || gameState == RocketGameState.CASHED_OUT) {
                            betAmount = amount
                        }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$amount",
                        color = if (isSelected) NeonCyan else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Button: Launch vs Cash Out
        if (gameState == RocketGameState.FLYING) {
            val potentialWin = (betAmount * currentMultiplier).toLong()
            Button(
                onClick = {
                    cashOutMultiplier = currentMultiplier
                    cashedOutWin = potentialWin
                    onUpdateCoins(potentialWin)
                    gameState = RocketGameState.CASHED_OUT
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("rocket_cashout_button"),
                colors = ButtonDefaults.buttonColors(containerColor = WinGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "CASH OUT ($potentialWin COINS 💰 @ %.2fx)".format(currentMultiplier),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
        } else {
            Button(
                onClick = {
                    val currentCoins = user?.coins ?: 0L
                    if (currentCoins < betAmount) {
                        return@Button
                    }

                    if (!onUpdateCoins(-betAmount)) return@Button

                    gameState = RocketGameState.FLYING
                    currentMultiplier = 1.00f
                    cashedOutWin = 0L

                    // Determine crash target
                    // Weighted distribution for realistic crash curve
                    val rand = Math.random()
                    val crashPoint = when {
                        rand < 0.20 -> 1.05f + (Math.random() * 0.40).toFloat() // early crash 1.05 - 1.45
                        rand < 0.65 -> 1.45f + (Math.random() * 2.50).toFloat() // common 1.45 - 3.95
                        rand < 0.90 -> 3.95f + (Math.random() * 6.00).toFloat() // good run 3.95 - 9.95
                        else -> 10.0f + (Math.random() * 25.00).toFloat() // mega moon 10x - 35x
                    }

                    scope.launch {
                        rocketY.snapTo(0f)
                        rocketX.snapTo(0f)

                        var mult = 1.00f
                        while (mult < crashPoint && gameState == RocketGameState.FLYING) {
                            val step = if (mult < 2.0f) 0.04f else if (mult < 5.0f) 0.08f else 0.18f
                            mult += step
                            currentMultiplier = mult
                            rocketY.animateTo(-50f * (mult / 8f).coerceAtMost(1.8f), tween(80, easing = LinearEasing))
                            delay(80)
                        }

                        if (gameState == RocketGameState.FLYING) {
                            // Crashed without cashing out
                            currentMultiplier = crashPoint
                            gameState = RocketGameState.CRASHED
                            recentCrashes.add(crashPoint)
                        } else if (gameState == RocketGameState.CASHED_OUT) {
                            recentCrashes.add(crashPoint)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("rocket_launch_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "LAUNCH ROCKET (Bet $betAmount 💰)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
        }
    }
}
