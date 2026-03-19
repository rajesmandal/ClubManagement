package com.sohitechnology.gymstudio.hammer.ui.admin.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sohitechnology.gymstudio.hammer.navigation.AppBottomBar
import com.sohitechnology.gymstudio.hammer.navigation.MainRoute
import com.sohitechnology.gymstudio.hammer.ui.common.AppTopBar
import com.sohitechnology.gymstudio.hammer.ui.common.EmptyState
import com.sohitechnology.gymstudio.hammer.ui.home.MemberCountCard
import com.sohitechnology.gymstudio.hammer.ui.member.MemberItem
import com.sohitechnology.gymstudio.hammer.ui.member.MemberUiModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AdminHomeScreen(
    navController: NavHostController,
    onMenuClick: () -> Unit = {},
    onMemberClick: (MemberUiModel) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: AdminHomeViewModel = hiltViewModel()
) {
    val memberCount by viewModel.memberCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val expiryMembers by viewModel.expiryMembers.collectAsState()
    val isExpiryLoading by viewModel.isExpiryLoading.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Admin Dashboard",
                onMenuClick = onMenuClick,
                onNotificationClick = {
                    navController.navigate(MainRoute.Notification.route)
                }
            )
        },
        bottomBar = {
            AppBottomBar(navController, role = "admin")
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && memberCount == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null && memberCount == null) {
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
                    memberCount?.let { data ->
                        MemberCountCard(data, onReload = { viewModel.reloadAll() }, isLoading = isLoading)
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
                }
            }
        }
    }
}
