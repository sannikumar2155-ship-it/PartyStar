package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ChatMessage
import com.example.data.FloatingEmoji
import com.example.data.GameType
import com.example.data.MicSeat
import com.example.data.PartyRoomData
import com.example.data.UserProfile
import com.example.ui.theme.DangerRed
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PartyBg
import com.example.ui.theme.PartyCard
import com.example.ui.theme.WinGreen

@Composable
fun PartyRoomScreen(
    room: PartyRoomData,
    user: UserProfile?,
    chatMessages: List<ChatMessage>,
    floatingEmojis: List<FloatingEmoji>,
    onLeaveRoom: () -> Unit,
    onTakeSeat: (Int) -> Unit,
    onToggleMicMute: () -> Unit,
    onToggleTalking: (Boolean) -> Unit,
    onLeaveSeat: () -> Unit,
    onSendMessage: (String) -> Unit,
    onTriggerEmoji: (String) -> Unit,
    onOpenGame: (GameType) -> Unit,
    onOpenSoulmate: () -> Unit,
    onOpenRecharge: () -> Unit,
    onOpenReport: () -> Unit
) {
    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto scroll chat
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val userSeat = room.seats.find { it.userId == user?.id }
    val isOnMic = userSeat != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyBg)
    ) {
        // Hero Background Stage Image
        Image(
            painter = painterResource(id = R.drawable.bg_party_banner),
            contentDescription = "Party Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .alpha(0.35f),
            contentScale = ContentScale.Crop
        )

        // Main Room Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLeaveRoom,
                        modifier = Modifier.testTag("leave_room_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Room", tint = Color.White)
                    }

                    Column {
                        Text(
                            text = room.title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("ID: ${room.id}", color = Color.Gray, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🔥 ${room.popularity}", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Quick Action Badges (Soulmate CP, Recharge, Report)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Soulmate CP Ring Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonPink.copy(alpha = 0.3f))
                            .border(1.dp, NeonPink, CircleShape)
                            .clickable { onOpenSoulmate() }
                            .testTag("room_soulmate_ring_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💍", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Coins / Recharge Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(GoldCoin.copy(alpha = 0.2f))
                            .border(1.dp, GoldCoin, RoundedCornerShape(16.dp))
                            .clickable { onOpenRecharge() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("room_recharge_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💰", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${user?.coins ?: 0}", color = GoldCoin, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("+", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Clean Community Report Button
                    IconButton(
                        onClick = onOpenReport,
                        modifier = Modifier.size(34.dp).testTag("room_report_button")
                    ) {
                        Icon(Icons.Default.Report, contentDescription = "Report", tint = DangerRed)
                    }
                }
            }

            // Announcement Ribbon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF280B3F).copy(alpha = 0.8f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📢", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = room.announcement,
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stage Mic Seats (8 Seats Grid: 2 rows of 4)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                val row1 = room.seats.take(4)
                val row2 = room.seats.drop(4).take(4)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row1.forEach { seat ->
                        MicSeatView(
                            seat = seat,
                            isSelf = seat.userId == user?.id,
                            onClick = { onTakeSeat(seat.index) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row2.forEach { seat ->
                        MicSeatView(
                            seat = seat,
                            isSelf = seat.userId == user?.id,
                            onClick = { onTakeSeat(seat.index) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Room Games Dock (Lucky 77, Fruity Party, Original 777, Rocket Launch, Teen Patti)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = NeonGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ROOM GAMES (TAP TO PLAY)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGold
                        )
                    }
                    Text("Multiplayer Odds", fontSize = 9.sp, color = NeonCyan)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GameType.values().forEach { game ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF3B1254), Color(0xFF1D062C))
                                    )
                                )
                                .border(1.dp, NeonPurple.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .clickable { onOpenGame(game) }
                                .padding(vertical = 6.dp, horizontal = 2.dp)
                                .testTag("game_dock_${game.name}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(game.icon, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = game.displayName,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chat Messages Feed
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(chatMessages, key = { it.id }) { msg ->
                        ChatBubble(msg = msg)
                    }
                }

                // Floating Emojis Layer
                floatingEmojis.forEach { fe ->
                    AnimatedFloatingEmoji(fe = fe)
                }
            }

            // Quick Floating Emoji Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val emojiTray = listOf("❤️", "🔥", "🎉", "😂", "🌹", "👑", "🚀", "💎", "💍")
                emojiTray.forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF26103E))
                            .border(1.dp, Color(0xFF4C217A), CircleShape)
                            .clickable { onTriggerEmoji(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 16.sp)
                    }
                }
            }

            // Bottom Input & Mic Control Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PartyCard)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Control Toggle
                if (isOnMic) {
                    IconButton(
                        onClick = onToggleMicMute,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (userSeat?.isMuted == true) DangerRed else WinGreen)
                            .testTag("toggle_mic_mute_button")
                    ) {
                        Icon(
                            if (userSeat?.isMuted == true) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))

                    // Step down button
                    IconButton(
                        onClick = onLeaveSeat,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("🏃", fontSize = 16.sp)
                    }
                } else {
                    // Tap to join mic shortcut
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonPurple.copy(alpha = 0.3f))
                            .border(1.dp, NeonPurple, RoundedCornerShape(8.dp))
                            .clickable { onTakeSeat(1) }
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Mic", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Chat Input Field
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = { Text("Say something in room...", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("room_chat_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Send Button
                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank()) {
                            onSendMessage(messageInput)
                            messageInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(NeonPink)
                        .testTag("send_chat_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun MicSeatView(
    seat: MicSeat,
    isSelf: Boolean,
    onClick: () -> Unit
) {
    val isOccupied = seat.userId != null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (isOccupied) Color(0xFF280F40) else Color(0xFF160724))
                .border(
                    2.dp,
                    if (seat.isTalking) NeonCyan else if (seat.hasSoulmateRing) NeonPink else if (isOccupied) NeonGold else Color(0xFF4C217A),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isOccupied) {
                Text(seat.userAvatar ?: "👤", fontSize = 24.sp)
                if (seat.hasSoulmateRing) {
                    // Soulmate ring badge on seat!
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(NeonPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💍", fontSize = 9.sp)
                    }
                }
                if (seat.isMuted) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(DangerRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MicOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                    Text("${seat.index}", fontSize = 9.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isOccupied) seat.userName ?: "Seat ${seat.index}" else "Seat ${seat.index}",
            fontSize = 10.sp,
            color = if (isSelf) NeonCyan else Color.LightGray,
            fontWeight = if (isSelf) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    if (msg.isSystem) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF2E103E).copy(alpha = 0.7f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${msg.senderAvatar} ${msg.text}",
                fontSize = 11.sp,
                color = NeonCyan,
                fontWeight = FontWeight.SemiBold
            )
        }
    } else if (msg.isGift) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(NeonPink.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
                .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "🎁 ${msg.text}",
                fontSize = 11.sp,
                color = NeonGold,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1B0728).copy(alpha = 0.75f))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(msg.senderAvatar, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = msg.senderName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPink
                )
                Text(
                    text = msg.text,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AnimatedFloatingEmoji(fe: FloatingEmoji) {
    val offsetY = remember { Animatable(300f) }
    val alphaVal = remember { Animatable(1f) }

    LaunchedEffect(fe.id) {
        offsetY.animateTo(
            targetValue = -150f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    LaunchedEffect(fe.id) {
        alphaVal.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 2200, delayMillis = 400)
        )
    }

    Box(
        modifier = Modifier
            .offset { IntOffset((fe.startXFraction * 600).toInt(), offsetY.value.toInt()) }
            .alpha(alphaVal.value)
    ) {
        Text(fe.emoji, fontSize = 28.sp)
    }
}
