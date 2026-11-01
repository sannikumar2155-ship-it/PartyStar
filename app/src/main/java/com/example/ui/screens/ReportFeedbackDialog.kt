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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.data.ReportTicket
import com.example.ui.theme.DangerRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PartyCard
import com.example.ui.theme.WinGreen

@Composable
fun ReportFeedbackDialog(
    initialTab: Int = 0, // 0 = Clean Report, 1 = Feedback & Support, 2 = My Tickets
    tickets: List<ReportTicket>,
    onClose: () -> Unit,
    onSubmitReport: (String, String, String, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    // Report Form States
    var targetType by remember { mutableStateOf("User") } // User or Room
    var targetId by remember { mutableStateOf("") }
    val reportReasons = listOf(
        "Abusive / Vulgar Speech",
        "Spam / Advertising in Chat",
        "Harassment / Bullying",
        "Recharge Scam / Fraud",
        "Underage Behavior",
        "Inappropriate Profile / Room"
    )
    var selectedReason by remember { mutableStateOf(reportReasons[0]) }
    var reportDetails by remember { mutableStateOf("") }
    var evidenceAttached by remember { mutableStateOf(false) }

    // Feedback Form States
    var feedbackCategory by remember { mutableStateOf("Room & Audio Experience") }
    var feedbackRating by remember { mutableIntStateOf(5) }
    var feedbackComments by remember { mutableStateOf("") }
    var feedbackSubmittedMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF260824), Color(0xFF140417), Color(0xFF09010C))
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
                Icon(Icons.Default.Security, contentDescription = null, tint = NeonCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CLEAN COMMUNITY & REPORT",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag("close_report_dialog")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1D0623))
                .padding(4.dp)
        ) {
            listOf("Clean Report", "Feedback & FAQs", "My Tickets (${tickets.size})").forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedTab == index) NeonPink else Color.Transparent)
                        .clickable { selectedTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> {
                // CLEAN REPORT FORM
                Text(
                    text = "Report Harmful / Inappropriate Behavior",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonGold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = "PartyStar is committed to a clean, safe, and family-friendly space. Violators face immediate mic mute & account ban.",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Report Target Type
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("User", "Party Room").forEach { type ->
                        val isSelected = targetType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) NeonPurple.copy(alpha = 0.4f) else Color(0xFF230729))
                                .border(1.dp, if (isSelected) NeonCyan else Color.DarkGray, RoundedCornerShape(8.dp))
                                .clickable { targetType = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Report $type", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetId,
                    onValueChange = { targetId = it },
                    label = { Text("Target Username or Room ID", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth().testTag("report_target_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonPink
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Violation Reason:",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                reportReasons.forEach { reason ->
                    val isSelected = selectedReason == reason
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) DangerRed.copy(alpha = 0.25f) else Color(0xFF1E0523))
                            .border(1.dp, if (isSelected) DangerRed else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { selectedReason = reason }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = reason,
                            color = if (isSelected) NeonGold else Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reportDetails,
                    onValueChange = { reportDetails = it },
                    label = { Text("Detailed Description (Context, timestamp, etc.)", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth().height(90.dp).testTag("report_details_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonPink
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Evidence Attach Button Simulation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF26082C))
                        .clickable { evidenceAttached = !evidenceAttached }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (evidenceAttached) "📎 Screenshot Evidence Attached (screenshot_log.png)" else "📎 Attach Chat / Audio Screenshot",
                        fontSize = 11.sp,
                        color = if (evidenceAttached) WinGreen else NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(if (evidenceAttached) "Remove" else "Attach", fontSize = 11.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val finalTarget = targetId.ifBlank { "Unknown_Offender" }
                        onSubmitReport(targetType, finalTarget, selectedReason, reportDetails.ifBlank { "Reported via clean community panel." })
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_report_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Report, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SUBMIT CLEAN REPORT", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            1 -> {
                // FEEDBACK & FAQS
                Text(
                    text = "User Feedback & Suggestions",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Star Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { feedbackRating = star }) {
                            Icon(
                                if (star <= feedbackRating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "$star Stars",
                                tint = NeonGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = feedbackComments,
                    onValueChange = { feedbackComments = it },
                    label = { Text("What can we improve in PartyStar?", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth().height(90.dp).testTag("feedback_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonCyan
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        feedbackSubmittedMsg = "Thank you! Your feedback has been shared with PartyStar dev team. ❤️"
                        feedbackComments = ""
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("submit_feedback_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Submit Feedback 💌", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                if (feedbackSubmittedMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(feedbackSubmittedMsg ?: "", color = WinGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FAQs
                Text(
                    text = "Frequently Asked Questions (FAQs):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonGold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                val faqs = listOf(
                    "How to recharge with UPI?" to "Open Wallet & Recharge, select any coin pack, pick GPay, PhonePe or Paytm UPI, and approve payment instantly.",
                    "What is Soulmate CP Ring?" to "Confess to your favorite party partner, forge a ring tier (Silver to Celestial), and unlock special intimacy perks and badge on mic seats.",
                    "How to open Family Room?" to "Join or create a Family Guild in the Family tab. Complete daily check-ins to unlock the clan treasure chest and level up!"
                )

                faqs.forEach { (q, a) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PartyCard)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(q, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(a, color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                }
            }

            2 -> {
                // MY REPORT TICKETS
                if (tickets.isEmpty()) {
                    Text("No tickets submitted yet. Thank you for keeping PartyStar clean!", color = Color.Gray, fontSize = 13.sp)
                } else {
                    tickets.forEach { ticket ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PartyCard)
                                .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(ticket.ticketId, fontWeight = FontWeight.Bold, color = NeonGold, fontSize = 13.sp)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (ticket.status == "Resolved") WinGreen.copy(alpha = 0.2f) else NeonPink.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            ticket.status,
                                            color = if (ticket.status == "Resolved") WinGreen else NeonPink,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Target: ${ticket.targetType} [${ticket.targetId}]", color = Color.White, fontSize = 12.sp)
                                Text("Reason: ${ticket.reason}", color = Color.LightGray, fontSize = 11.sp)
                                if (ticket.details.isNotBlank()) {
                                    Text("Details: \"${ticket.details}\"", color = Color.Gray, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
