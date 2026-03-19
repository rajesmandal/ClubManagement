package com.sohitechnology.gymstudio.hammer.ui.member.home

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
import com.sohitechnology.gymstudio.hammer.ui.home.MemberCalendarCard
import com.sohitechnology.gymstudio.hammer.ui.home.MemberDashboardCard
import com.sohitechnology.gymstudio.hammer.ui.home.MonthlyInsightsCard
import com.sohitechnology.gymstudio.hammer.ui.home.PlanDetailsCard
import com.sohitechnology.gymstudio.hammer.ui.home.UsageDonutChart
import com.sohitechnology.gymstudio.hammer.ui.member.report.MemberReportViewModel

@Composable
fun MemberHomeScreen(
    navController: NavHostController,
    onMenuClick: () -> Unit = {},
    viewModel: MemberHomeViewModel = hiltViewModel(),
    reportViewModel: MemberReportViewModel = hiltViewModel()
) {
    val memberDetail by viewModel.memberDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val usageStats by reportViewModel.usageStats.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Member Home",
                onMenuClick = onMenuClick,
                onNotificationClick = {
                    navController.navigate(MainRoute.Notification.route)
                }
            )
        },
        bottomBar = {
            AppBottomBar(navController, role = "member")
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Check if we have data to show
            if (memberDetail != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    MemberDashboardCard(memberDetail!!, onReload = { viewModel.getMemberDetail() }, isLoading = isLoading)

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    UsageDonutChart(usageStats)

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    MonthlyInsightsCard(usageStats)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Attendance Calendar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                    )
                    
                    MemberCalendarCard(usageStats.activeDates)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Your Current Plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                    )

                    memberDetail!!.getMemberTransaction?.firstOrNull()?.let { transaction ->
                        PlanDetailsCard(transaction)
                    } ?: EmptyState(
                        title = "No Active Plan",
                        description = "Please contact your club to renew your membership."
                    )
                }
            } else if (isLoading) {
                // Only show loading if we don't have data yet
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                // Show error if data load failed
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error ?: "Unknown Error", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.getMemberDetail() }) {
                        Text("Retry")
                    }
                }
            } else {
                // Fallback for unexpected empty state
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data available")
                }
            }
        }
    }
}
