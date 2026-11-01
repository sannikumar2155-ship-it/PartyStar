package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameType
import com.example.data.PartyRoomData
import com.example.data.UserProfile
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PartyBg
import com.example.ui.theme.PartyCard

@Composable
fun HomeScreen(
    user: UserProfile?,
    rooms: List<PartyRoomData>,
    onSelectRoom: (PartyRoomData) -> Unit,
    onOpenSoulmate: () -> Unit,
    onOpenRecharge: () -> Unit,
    onOpenReport: () -> Unit,
    onOpenGame: (GameType) -> Unit,
    onLogout: () -> Unit,
    selectedNavTab: Int,
    onNavTabSelected: (Int) -> Unit
) {
    var roomCategoryFilter by remember { mutableIntStateOf(0) } // 0 = All, 1 = Music, 2 = CP Love, 3 = Gaming

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyBg)
    ) {
        // Top App Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PartyCard)
                .padding(top = 40.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar & Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2C0D48))
                            .border(1.5.dp, NeonGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user?.avatar ?: "👑", fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user?.name ?: "Guest",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (user?.soulmate != null) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("💍", fontSize = 12.sp)
                            }
                        }
                        Text(
                            text = "VIP ${user?.vipLevel ?: 1} • ${user?.loginMethod?.label ?: ""}",
                            color = NeonCyan,
                            fontSize = 10.sp
                        )
                    }
                }

                // Balance & Recharge Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(GoldCoin.copy(alpha = 0.2f))
                            .border(1.dp, GoldCoin, RoundedCornerShape(16.dp))
                            .clickable { onOpenRecharge() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("home_recharge_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💰", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${user?.coins ?: 0}",
                                color = GoldCoin,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(NeonGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onOpenReport,
                        modifier = Modifier.size(34.dp).testTag("home_report_button")
                    ) {
                        Icon(Icons.Default.Report, contentDescription = "Report & Feedback", tint = Color.LightGray)
                    }
                }
            }
        }

        // Main Body Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Soulmate CP Interactive Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF6B0E5D), Color(0xFF32083D))
                            )
                        )
                        .border(1.dp, NeonPink, RoundedCornerShape(18.dp))
                        .clickable { onOpenSoulmate() }
                        .padding(14.dp)
                        .testTag("home_soulmate_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_soulmate_ring),
                                contentDescription = "Soulmate Ring",
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, NeonGold, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (user?.soulmate != null) "Soulmate: ${user.soulmate.partnerName} 💍" else "Forge Soulmate Ring & CP Confession 💍",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (user?.soulmate != null) "${user.soulmate.ringTier.title} • ${user.soulmate.intimacyScore} CP Points"
                                    else "Wear couple ring badge on stage & share perks!",
                                    fontSize = 11.sp,
                                    color = NeonPink
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonPink)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Open", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quick Games Strip
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎮 MINI-GAMES LOUNGE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGold
                        )
                        Text(
                            text = "Play for Coins",
                            fontSize = 11.sp,
                            color = NeonCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GameType.values().forEach { game ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PartyCard)
                                    .border(1.dp, Color(0xFF3E1763), RoundedCornerShape(12.dp))
                                    .clickable { onOpenGame(game) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp)
                                    .testTag("home_game_card_${game.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(game.icon, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = game.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Room Category Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All Rooms 🔥", "Music & Party 🎶", "CP Romance ❤️", "Gaming Tables 🎲").forEachIndexed { index, label ->
                        val isSelected = roomCategoryFilter == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) NeonPurple else Color(0xFF220935))
                                .clickable { roomCategoryFilter = index }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else Color.LightGray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Party Rooms List
            items(rooms) { room ->
                RoomCard(room = room, onJoin = { onSelectRoom(room) })
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun RoomCard(
    room: PartyRoomData,
    onJoin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PartyCard)
            .border(1.dp, Color(0xFF381559), RoundedCornerShape(16.dp))
            .clickable { onJoin() }
            .padding(14.dp)
            .testTag("room_card_${room.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E0F4D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(room.hostAvatar, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = room.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Host: ${room.hostName}", fontSize = 11.sp, color = Color.LightGray)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("• ${room.tag}", fontSize = 11.sp, color = NeonCyan)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonPink.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("🔥 ${room.popularity}", fontSize = 11.sp, color = NeonPink, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mic Seats preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    room.seats.take(6).forEach { seat ->
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(if (seat.userId != null) Color(0xFF4C217A) else Color(0xFF1E0A33)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (seat.userId != null) {
                                Text(seat.userAvatar ?: "👤", fontSize = 12.sp)
                            } else {
                                Text("🎤", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                Button(
                    onClick = onJoin,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Join Party", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
