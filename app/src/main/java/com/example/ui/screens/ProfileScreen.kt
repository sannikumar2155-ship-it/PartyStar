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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.UserProfile
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DiamondBlue
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PartyBg
import com.example.ui.theme.PartyCard
import com.example.ui.theme.WinGreen

@Composable
fun ProfileScreen(
    user: UserProfile?,
    onOpenRecharge: () -> Unit,
    onOpenSoulmate: () -> Unit,
    onOpenReport: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "MY PROFILE & ACCOUNT",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Profile Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF381254), Color(0xFF1B072B))
                    )
                )
                .border(1.5.dp, NeonPurple, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2B0A42))
                            .border(2.dp, NeonGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user?.avatar ?: "👑", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user?.name ?: "StarUser",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonGold)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("VIP ${user?.vipLevel ?: 1}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }

                        Text("ID: ${user?.id ?: "000000"}", color = Color.LightGray, fontSize = 11.sp)
                        Text(
                            text = "Login: ${user?.loginMethod?.label ?: "Mobile"} • ${user?.phone ?: user?.email ?: "Active"}",
                            color = NeonCyan,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wallet Balances Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF130420))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Coins Balance", fontSize = 10.sp, color = Color.LightGray)
                        Text("${user?.coins ?: 0} 💰", fontSize = 16.sp, fontWeight = FontWeight.Black, color = GoldCoin)
                    }

                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(Color.DarkGray))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Diamonds", fontSize = 10.sp, color = Color.LightGray)
                        Text("${user?.diamonds ?: 0} 💎", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DiamondBlue)
                    }

                    Button(
                        onClick = onOpenRecharge,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp).testTag("profile_recharge_button")
                    ) {
                        Text("Add Funds", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Soulmate Status
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(PartyCard)
                .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { onOpenSoulmate() }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_soulmate_ring),
                        contentDescription = "Soulmate",
                        modifier = Modifier.size(40.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (user?.soulmate != null) "Soulmate: ${user.soulmate.partnerName} 💍" else "No Soulmate Bound Yet",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (user?.soulmate != null) "${user.soulmate.ringTier.title} • ${user.soulmate.intimacyScore} CP Pts"
                            else "Tap to forge ring & declare love in room!",
                            color = NeonPink,
                            fontSize = 11.sp
                        )
                    }
                }
                Text("Manage >", fontSize = 11.sp, color = NeonCyan)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Family Status
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(PartyCard)
                .border(1.dp, Color(0xFF4C217A), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user?.family?.familyBadge ?: "⚔️", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = user?.family?.familyName ?: "Royal Warriors Clan",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Level ${user?.family?.familyLevel ?: 6} Clan • Role: ${user?.family?.role ?: "Member"}",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(WinGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Active", color = WinGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Options List
        Text(
            text = "Community & Support",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            Triple(Icons.Default.Security, "Clean Room & Community Guidelines", "Safe chat and anti-harassment rules"),
            Triple(Icons.Default.Report, "Clean Report & User Violation", "Submit or track your reports"),
            Triple(Icons.Default.HelpOutline, "Feedback, FAQs & Help Center", "Direct developer support")
        ).forEach { (icon, title, desc) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PartyCard)
                    .clickable { onOpenReport() }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text(desc, color = Color.LightGray, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("logout_button"),
            colors = ButtonDefaults.buttonColors(containerColor = DangerRed.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text("SWITCH ACCOUNT / LOGOUT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
