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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.RingTier
import com.example.data.UserProfile
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PartyCard

@Composable
fun SoulmateDialog(
    user: UserProfile?,
    onClose: () -> Unit,
    onUpgradeRing: (RingTier, String) -> Unit,
    onSendGift: (String, Long, Int) -> Unit
) {
    var selectedTier by remember { mutableStateOf(RingTier.DIAMOND) }
    var partnerInput by remember { mutableStateOf(user?.soulmate?.partnerName ?: "Princess_Simran") }
    var showProposeSection by remember { mutableStateOf(user?.soulmate == null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF380838), Color(0xFF1F0523), Color(0xFF0F0212))
                )
            )
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
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
                    text = "💍 SOULMATE CP RING",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonPink
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag("close_soulmate_dialog")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Graphic & Ring
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF5A145A), Color(0xFF260528))
                    )
                )
                .border(2.dp, NeonPink, RoundedCornerShape(20.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Ring Image Asset
                Image(
                    painter = painterResource(id = R.drawable.ic_soulmate_ring),
                    contentDescription = "Soulmate Ring",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, NeonGold, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = user?.soulmate?.ringTier?.title ?: "Forge Your Soulmate Ring",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonGold
                )

                Text(
                    text = if (user?.soulmate != null) "Bound in Love • ${user.soulmate.daysTogether} Days Together"
                    else "Connect with your CP partner, wear the ring badge on voice seats & enjoy room entrance auras!",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Couple Profile if Soulmate exists
        if (user?.soulmate != null) {
            val sm = user.soulmate
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PartyCard)
                    .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(user.avatar, fontSize = 28.sp)
                            Text(" ❤️ ", fontSize = 20.sp)
                            Text(sm.partnerAvatar, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "${user.name} & ${sm.partnerName}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text("Intimacy: ${sm.intimacyScore} CP Pts", color = NeonCyan, fontSize = 11.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonPink.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("LV. ${(sm.intimacyScore / 1000) + 1}", color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { ((sm.intimacyScore % 1000) / 1000f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = NeonPink,
                        trackColor = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "\"${sm.confessionNote}\"",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CP Quick Gifts
            Text(
                text = "Send Romantic CP Gifts (Boost Intimacy):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val cpGifts = listOf(
                    Triple("99 Roses 🌹", 500L, 100),
                    Triple("Crystal Ring 💍", 2000L, 500),
                    Triple("Love Castle 🏰", 5000L, 1500)
                )
                cpGifts.forEach { (name, cost, intimacy) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2E0C35))
                            .border(1.dp, NeonPurple, RoundedCornerShape(12.dp))
                            .clickable { onSendGift(name, cost, intimacy) }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(name.take(2), fontSize = 24.sp)
                            Text(name.drop(2), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("$cost 💰", fontSize = 11.sp, color = GoldCoin, fontWeight = FontWeight.Bold)
                            Text("+$intimacy CP", fontSize = 9.sp, color = NeonPink)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Upgrade / Forge Ring Section
        Text(
            text = "Select Ring Tier to Forge / Upgrade:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        RingTier.values().forEach { tier ->
            val isSelected = selectedTier == tier
            val tierCost = when (tier) {
                RingTier.SILVER -> 5000L
                RingTier.GOLD -> 15000L
                RingTier.DIAMOND -> 30000L
                RingTier.CELESTIAL -> 75000L
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) NeonPink.copy(alpha = 0.25f) else Color(0xFF220828))
                    .border(
                        1.dp,
                        if (isSelected) NeonGold else Color(0xFF4A194E),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedTier = tier }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = tier.title,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NeonGold else Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            text = tier.bonus,
                            color = NeonCyan,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "$tierCost 💰",
                        color = GoldCoin,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Partner name input if no soulmate or wants to customize
        OutlinedTextField(
            value = partnerInput,
            onValueChange = { partnerInput = it },
            label = { Text("Soulmate / CP Partner Username", color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("soulmate_partner_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = NeonPink,
                unfocusedBorderColor = Color.DarkGray
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onUpgradeRing(selectedTier, partnerInput)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("forge_soulmate_ring_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "FORGE ${selectedTier.name} RING 💍",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}
