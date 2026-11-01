package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.GameType
import com.example.ui.games.FruityPartyGame
import com.example.ui.games.Lucky77Game
import com.example.ui.games.Original777Game
import com.example.ui.games.RocketLaunchGame
import com.example.ui.games.TeenPattiGame
import com.example.ui.screens.FamilyScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.PartyRoomScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RechargeDialog
import com.example.ui.screens.ReportFeedbackDialog
import com.example.ui.screens.SoulmateDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PartyBg
import com.example.ui.theme.PartyCard
import com.example.ui.viewmodel.PartyViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppHost()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppHost(vm: PartyViewModel = viewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val user by vm.currentUser.collectAsStateWithLifecycle()
    val rooms by vm.rooms.collectAsStateWithLifecycle()
    val familyRooms by vm.familyRooms.collectAsStateWithLifecycle()
    val activeRoom by vm.activeRoom.collectAsStateWithLifecycle()
    val chatMessages by vm.chatMessages.collectAsStateWithLifecycle()
    val floatingEmojis by vm.floatingEmojis.collectAsStateWithLifecycle()
    val reports by vm.reports.collectAsStateWithLifecycle()
    val transactions by vm.transactions.collectAsStateWithLifecycle()

    val activeGame by vm.activeGame.collectAsStateWithLifecycle()
    val showRechargeSheet by vm.showRechargeSheet.collectAsStateWithLifecycle()
    val showSoulmateSheet by vm.showSoulmateSheet.collectAsStateWithLifecycle()
    val showReportSheet by vm.showReportSheet.collectAsStateWithLifecycle()

    var selectedNavTab by remember { mutableIntStateOf(0) }

    // Toast/Snackbar notifications
    LaunchedEffect(Unit) {
        vm.uiEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Predictive back / backstack handling
    BackHandler(enabled = true) {
        when {
            activeGame != null -> vm.closeGame()
            showRechargeSheet -> vm.toggleRechargeSheet(false)
            showSoulmateSheet -> vm.toggleSoulmateSheet(false)
            showReportSheet -> vm.toggleReportSheet(false)
            activeRoom != null -> vm.leaveRoom()
            selectedNavTab != 0 -> selectedNavTab = 0
            else -> {
                // Exit app or toast
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(PartyBg)) {
        if (user == null) {
            LoginScreen(
                onLogin = { method, identifier, name ->
                    vm.login(method, identifier, name)
                }
            )
        } else if (activeRoom != null) {
            val r = activeRoom!!
            PartyRoomScreen(
                room = r,
                user = user,
                chatMessages = chatMessages,
                floatingEmojis = floatingEmojis,
                onLeaveRoom = { vm.leaveRoom() },
                onTakeSeat = { seatIdx -> vm.occupyMicSeat(seatIdx) },
                onToggleMicMute = { vm.toggleMicMute() },
                onToggleTalking = { talking -> vm.toggleTalking(talking) },
                onLeaveSeat = { vm.leaveMicSeat() },
                onSendMessage = { text -> vm.sendChatMessage(text) },
                onTriggerEmoji = { emoji -> vm.triggerFloatingEmoji(emoji) },
                onOpenGame = { game -> vm.openGame(game) },
                onOpenSoulmate = { vm.toggleSoulmateSheet(true) },
                onOpenRecharge = { vm.toggleRechargeSheet(true) },
                onOpenReport = { vm.toggleReportSheet(true) }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = PartyBg,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    NavigationBar(
                        containerColor = PartyCard,
                        contentColor = Color.White,
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        NavigationBarItem(
                            selected = selectedNavTab == 0,
                            onClick = { selectedNavTab = 0 },
                            icon = { Icon(Icons.Default.MeetingRoom, contentDescription = "Party Rooms") },
                            label = { Text("Party", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonPink,
                                selectedTextColor = NeonPink,
                                indicatorColor = NeonPurple.copy(alpha = 0.3f),
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.LightGray
                            )
                        )
                        NavigationBarItem(
                            selected = selectedNavTab == 1,
                            onClick = { selectedNavTab = 1 },
                            icon = { Icon(Icons.Default.Shield, contentDescription = "Family Guild") },
                            label = { Text("Family", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonCyan,
                                selectedTextColor = NeonCyan,
                                indicatorColor = NeonCyan.copy(alpha = 0.2f),
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.LightGray
                            )
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = { vm.toggleRechargeSheet(true) },
                            icon = { Icon(Icons.Default.Payment, contentDescription = "Recharge") },
                            label = { Text("Recharge", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonPink,
                                unselectedIconColor = Color(0xFFFFB703),
                                unselectedTextColor = Color(0xFFFFB703)
                            )
                        )
                        NavigationBarItem(
                            selected = selectedNavTab == 2,
                            onClick = { selectedNavTab = 2 },
                            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonPink,
                                selectedTextColor = NeonPink,
                                indicatorColor = NeonPurple.copy(alpha = 0.3f),
                                unselectedIconColor = Color.LightGray,
                                unselectedTextColor = Color.LightGray
                            )
                        )
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    when (selectedNavTab) {
                        0 -> HomeScreen(
                            user = user,
                            rooms = rooms,
                            onSelectRoom = { selectedRoom -> vm.joinRoom(selectedRoom) },
                            onOpenSoulmate = { vm.toggleSoulmateSheet(true) },
                            onOpenRecharge = { vm.toggleRechargeSheet(true) },
                            onOpenReport = { vm.toggleReportSheet(true) },
                            onOpenGame = { game -> vm.openGame(game) },
                            onLogout = { vm.logout() },
                            selectedNavTab = selectedNavTab,
                            onNavTabSelected = { selectedNavTab = it }
                        )
                        1 -> FamilyScreen(
                            user = user,
                            familyRooms = familyRooms,
                            onEnterFamilyRoom = { fRoom -> vm.joinRoom(fRoom) },
                            onDailyCheckIn = { vm.familyCheckIn() }
                        )
                        2 -> ProfileScreen(
                            user = user,
                            onOpenRecharge = { vm.toggleRechargeSheet(true) },
                            onOpenSoulmate = { vm.toggleSoulmateSheet(true) },
                            onOpenReport = { vm.toggleReportSheet(true) },
                            onLogout = { vm.logout() }
                        )
                    }
                }
            }
        }

        // Room Games Bottom Sheet Modal
        if (activeGame != null) {
            ModalBottomSheet(
                onDismissRequest = { vm.closeGame() },
                containerColor = Color.Transparent,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                when (activeGame!!) {
                    GameType.LUCKY_77 -> Lucky77Game(
                        user = user,
                        onClose = { vm.closeGame() },
                        onUpdateCoins = { delta -> vm.updateCoins(delta) }
                    )
                    GameType.FRUITY_PARTY -> FruityPartyGame(
                        user = user,
                        onClose = { vm.closeGame() },
                        onUpdateCoins = { delta -> vm.updateCoins(delta) }
                    )
                    GameType.ORIGINAL_777 -> Original777Game(
                        user = user,
                        onClose = { vm.closeGame() },
                        onUpdateCoins = { delta -> vm.updateCoins(delta) }
                    )
                    GameType.ROCKET_LAUNCH -> RocketLaunchGame(
                        user = user,
                        onClose = { vm.closeGame() },
                        onUpdateCoins = { delta -> vm.updateCoins(delta) }
                    )
                    GameType.TEEN_PATTI -> TeenPattiGame(
                        user = user,
                        onClose = { vm.closeGame() },
                        onUpdateCoins = { delta -> vm.updateCoins(delta) }
                    )
                }
            }
        }

        // Recharge Bottom Sheet Modal
        if (showRechargeSheet) {
            ModalBottomSheet(
                onDismissRequest = { vm.toggleRechargeSheet(false) },
                containerColor = Color.Transparent,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                RechargeDialog(
                    user = user,
                    packs = vm.repository.rechargePacks,
                    paymentOptions = vm.repository.paymentOptions,
                    transactions = transactions,
                    onClose = { vm.toggleRechargeSheet(false) },
                    onConfirmRecharge = { pack, method ->
                        vm.processRecharge(pack, method)
                    }
                )
            }
        }

        // Soulmate CP Dialog
        if (showSoulmateSheet) {
            ModalBottomSheet(
                onDismissRequest = { vm.toggleSoulmateSheet(false) },
                containerColor = Color.Transparent,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                SoulmateDialog(
                    user = user,
                    onClose = { vm.toggleSoulmateSheet(false) },
                    onUpgradeRing = { tier, partner ->
                        vm.upgradeSoulmateRing(tier, partner)
                    },
                    onSendGift = { giftName, cost, intimacy ->
                        vm.sendSoulmateGift(giftName, cost, intimacy)
                    }
                )
            }
        }

        // Clean Report & Feedback Dialog
        if (showReportSheet) {
            ModalBottomSheet(
                onDismissRequest = { vm.toggleReportSheet(false) },
                containerColor = Color.Transparent,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                ReportFeedbackDialog(
                    initialTab = 0,
                    tickets = reports,
                    onClose = { vm.toggleReportSheet(false) },
                    onSubmitReport = { targetType, targetId, reason, details ->
                        vm.submitReport(targetType, targetId, reason, details)
                    }
                )
            }
        }
    }
}
