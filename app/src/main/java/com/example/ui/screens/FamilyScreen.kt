package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FamilyRoomData
import com.example.data.PartyRoomData
import com.example.data.UserProfile
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PartyBg
import com.example.ui.theme.PartyCard
import com.example.ui.theme.WinGreen

@Composable
fun FamilyScreen(
    user: UserProfile?,
    familyRooms: List<FamilyRoomData>,
    onEnterFamilyRoom: (PartyRoomData) -> Unit,
    onDailyCheckIn: () -> Unit
) {
    val myFamily = user?.family

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyBg)
            .padding(16.dp)
    ) {
        // Family Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = NeonGold, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FAMILY GUILDS & ROOMS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonPurple.copy(alpha = 0.3f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Clan Level ${myFamily?.familyLevel ?: 1}", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // My Family Card & Daily Chest
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF381254), Color(0xFF190626))
                    )
                )
                .border(1.5.dp, NeonGold, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(myFamily?.familyBadge ?: "⚔️", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = myFamily?.familyName ?: "Royal Warriors",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Role: ${myFamily?.role ?: "Member"} • ${myFamily?.weeklyExp ?: 28000} EXP",
                                fontSize = 11.sp,
                                color = NeonCyan
                            )
                        }
                    }

                    Button(
                        onClick = onDailyCheckIn,
                        colors = ButtonDefaults.buttonColors(containerColor = WinGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("family_checkin_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Check-In", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Clan Treasure Chest
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Family Treasure Chest (LV. 4)", fontSize = 12.sp, color = NeonGold, fontWeight = FontWeight.Bold)
                    }
                    Text("78% Unlocked", fontSize = 11.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { 0.78f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = NeonPink,
                    trackColor = Color(0xFF220831)
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Weekly rewards: 100,000 Coins split among active clan members! 💰",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Active Family Voice Rooms:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(familyRooms) { fam ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PartyCard)
                        .border(1.dp, Color(0xFF4C217A), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
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
                                    .background(Color(0xFF260A38)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🛡️", fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = fam.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = fam.motto,
                                    fontSize = 10.sp,
                                    color = Color.LightGray,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${fam.members} Members • Leader: ${fam.leaderName}",
                                    fontSize = 10.sp,
                                    color = NeonCyan
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val dummyRoom = PartyRoomData(
                                    id = fam.id,
                                    title = "⚔️ ${fam.name} Clan Room",
                                    hostName = fam.leaderName,
                                    hostAvatar = "🛡️",
                                    tag = "⚔️ Family Guild",
                                    popularity = 18200,
                                    isFamilyRoom = true,
                                    announcement = fam.motto,
                                    seats = (1..8).map { com.example.data.MicSeat(it) }
                                )
                                onEnterFamilyRoom(dummyRoom)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("enter_family_room_${fam.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Enter", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
