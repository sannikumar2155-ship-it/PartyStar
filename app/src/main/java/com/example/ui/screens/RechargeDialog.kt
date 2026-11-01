package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.PaymentCategory
import com.example.data.PaymentOption
import com.example.data.RechargePack
import com.example.data.RechargeReceipt
import com.example.data.UserProfile
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PartyCard
import com.example.ui.theme.WinGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RechargeDialog(
    user: UserProfile?,
    packs: List<RechargePack>,
    paymentOptions: List<PaymentOption>,
    transactions: List<RechargeReceipt>,
    onClose: () -> Unit,
    onConfirmRecharge: (RechargePack, PaymentOption) -> Unit
) {
    var selectedPack by remember { mutableStateOf(packs.firstOrNull { it.amountInr == 100 } ?: packs[0]) }
    var selectedMethod by remember { mutableStateOf(paymentOptions[0]) }
    var customUpiId by remember { mutableStateOf("user@okhdfcbank") }
    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessReceipt by remember { mutableStateOf<RechargeReceipt?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Recharge Packs, 1 = Txn History

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1E0A3C), Color(0xFF0F0420), Color(0xFF07020E))
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
                    text = "💰 WALLET & UPI RECHARGE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonGold
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag("close_recharge_dialog")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Balance Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Coins Balance", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    Text(
                        text = "${user?.coins ?: 0} 💰",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Diamonds: ${user?.diamonds ?: 0} 💎 • VIP ${user?.vipLevel ?: 1}",
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Instant UPI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs: Recharge vs History
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1B0833))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedTab == 0) NeonPurple else Color.Transparent)
                    .clickable { selectedTab = 0 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add Funds",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedTab == 1) NeonPurple else Color.Transparent)
                    .clickable { selectedTab = 1 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "History (${transactions.size})",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 1) {
            // Transaction History
            if (transactions.isEmpty()) {
                Text("No previous transactions found.", color = Color.Gray, fontSize = 13.sp)
            } else {
                transactions.forEach { txn ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PartyCard)
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(txn.method, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text(txn.txnId, color = Color.Gray, fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹${txn.amountInr}", color = NeonGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("+${txn.coinsAdded} Coins", color = WinGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Select Pack Section
            Text(
                text = "Select Coin Pack:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 2-column grid of packs
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                packs.chunked(2).forEach { rowPacks ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowPacks.forEach { pack ->
                            val isSelected = selectedPack.id == pack.id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) NeonPurple.copy(alpha = 0.35f) else Color(0xFF22093F))
                                    .border(
                                        1.5.dp,
                                        if (isSelected) NeonGold else Color(0xFF381561),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedPack = pack }
                                    .padding(10.dp)
                            ) {
                                Column {
                                    if (pack.tag != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(NeonPink)
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(pack.tag, fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                    Text(
                                        text = "₹${pack.amountInr}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = NeonGold
                                    )
                                    Text(
                                        text = "${pack.coins} Coins",
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (pack.bonusCoins > 0) {
                                        Text(
                                            text = "+${pack.bonusCoins} Bonus",
                                            fontSize = 10.sp,
                                            color = WinGreen,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Options (UPI & Wallets)
            Text(
                text = "Select UPI / Wallet Method:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            paymentOptions.forEach { option ->
                val isSelected = selectedMethod.id == option.id
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color(0xFF1E0A36))
                        .border(
                            1.dp,
                            if (isSelected) NeonCyan else Color(0xFF3B1566),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedMethod = option }
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(option.iconEmoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = option.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = option.subtitle,
                                    fontSize = 10.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            if (selectedMethod.id == "bhim") {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customUpiId,
                    onValueChange = { customUpiId = it },
                    label = { Text("Enter UPI ID (e.g. name@upi)", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonCyan
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Pay Button
            Button(
                onClick = {
                    isProcessing = true
                    scope.launch {
                        delay(1200) // Realistic gateway handshake
                        onConfirmRecharge(selectedPack, selectedMethod)
                        isProcessing = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("confirm_recharge_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                shape = RoundedCornerShape(12.dp),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Processing UPI Payment...", color = Color.Black, fontWeight = FontWeight.Bold)
                } else {
                    Text(
                        text = "PAY ₹${selectedPack.amountInr} VIA ${selectedMethod.name.uppercase()}",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
