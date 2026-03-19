package com.sohitechnology.gymstudio.hammer.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sohitechnology.gymstudio.hammer.data.model.MemberCountData
import com.sohitechnology.gymstudio.hammer.data.model.MemberDetailData
import com.sohitechnology.gymstudio.hammer.navigation.AppBottomBar
import com.sohitechnology.gymstudio.hammer.navigation.MainRoute
import com.sohitechnology.gymstudio.hammer.ui.common.AppTopBar
import com.sohitechnology.gymstudio.hammer.ui.common.EmptyState
import com.sohitechnology.gymstudio.hammer.ui.member.MemberItem
import com.sohitechnology.gymstudio.hammer.ui.member.MemberUiModel
import com.sohitechnology.gymstudio.hammer.ui.theme.ClubManagementTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onMenuClick: () -> Unit = {},
    onMemberClick: (MemberUiModel) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val memberCount by viewModel.memberCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val expiryMembers by viewModel.expiryMembers.collectAsState()
    val isExpiryLoading by viewModel.isExpiryLoading.collectAsState()
    
    val isAdmin by viewModel.isAdmin.collectAsState()
    val memberDetail by viewModel.memberDetail.collectAsState()

    // Permission Request Logic
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        var hasNotificationPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                hasNotificationPermission = isGranted
            }
        )

        LaunchedEffect(Unit) {
            if (!hasNotificationPermission) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    HomeContent(
        isAdmin = isAdmin,
        memberCount = memberCount,
        memberDetail = memberDetail,
        isLoading = isLoading,
        error = error,
        expiryMembers = expiryMembers,
        isExpiryLoading = isExpiryLoading,
        navController = navController,
        onMenuClick = onMenuClick,
        onMemberClick = onMemberClick,
        onReload = { viewModel.reloadAll() },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        role = if (isAdmin) "admin" else "member"
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeContent(
    isAdmin: Boolean,
    memberCount: MemberCountData?,
    memberDetail: MemberDetailData?,
    isLoading: Boolean,
    error: String?,
    expiryMembers: List<MemberUiModel>,
    isExpiryLoading: Boolean,
    navController: NavHostController,
    onMenuClick: () -> Unit,
    onMemberClick: (MemberUiModel) -> Unit,
    onReload: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    role: String
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Home",
                onMenuClick = onMenuClick,
                onNotificationClick = {
                    navController.navigate(MainRoute.Notification.route)
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                navController,
                role = role
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && memberCount == null && memberDetail == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null && memberCount == null && memberDetail == null) {
                Text(
                    text = error ?: "Unknown Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    if (isAdmin) {
                        memberCount?.let { data ->
                            MemberCountCard(data, onReload, isLoading)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Upcoming Expiries",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                        )
                        
                        if (isExpiryLoading && expiryMembers.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(30.dp))
                            }
                        } else if (expiryMembers.isEmpty()) {
                            EmptyState(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                title = "No Expiries Soon",
                                description = "No members are expiring in the near future."
                            )
                        } else {
                            expiryMembers.forEach { member ->
                                MemberItem(
                                    member = member,
                                    onClick = { onMemberClick(member) },
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            }
                        }
                    } else {
                        // Member UI
                        memberDetail?.let { data ->
                            MemberDashboardCard(data, onReload, isLoading)
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "Your Current Plan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                            )
                            
                            data.getMemberTransaction?.firstOrNull()?.let { transaction ->
                                PlanDetailsCard(transaction)
                            } ?: EmptyState(
                                title = "No Active Plan",
                                description = "Please contact your club to renew your membership."
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val mockMemberCount = MemberCountData(
        active = 50,
        deactive = 10,
        expired = 5,
        all = 65,
        todayRenew = 2
    )

    val mockExpiryMembers = listOf(
        MemberUiModel(
            id = 1,
            memberId = "M001",
            name = "John Doe",
            userName = "johndoe",
            password = "",
            image = "",
            status = "Active",
            gender = "Male",
            contactNo = "1234567890",
            emailId = "john@example.com",
            clubName = "Main Club",
            clubId = 1,
            birthDay = "01-01-1990",
            hireDay = "01-01-2023",
            address = "123 Street",
            nationality = "Indian",
            startDate = "01-01-2024",
            expiryDate = "01-02-2024"
        )
    )

    ClubManagementTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                HomeContent(
                    isAdmin = true,
                    memberCount = mockMemberCount,
                    memberDetail = null,
                    isLoading = false,
                    error = null,
                    expiryMembers = mockExpiryMembers,
                    isExpiryLoading = false,
                    navController = rememberNavController(),
                    onMenuClick = {},
                    onMemberClick = {},
                    onReload = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility,
                    role = "admin"
                )
            }
        }
    }
}
