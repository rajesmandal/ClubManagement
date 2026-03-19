package com.sohitechnology.gymstudio.hammer.ui.admin.report

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sohitechnology.gymstudio.hammer.ui.member.MemberFilterViewModel
import com.sohitechnology.gymstudio.hammer.ui.report.ReportTabContent

@Composable
fun AdminReportScreen(
    navController: NavHostController,
    onMenuClick: () -> Unit = {},
    viewModel: AdminReportViewModel = hiltViewModel(),
    filterViewModel: MemberFilterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val clubs by filterViewModel.clubs.collectAsState()
    
    ReportTabContent(
        state = state,
        clubs = clubs,
        navController = navController,
        onMenuClick = onMenuClick,
        onLoadMembers = { viewModel.loadMembers(it) },
        onGetReports = { clubId, memberIds, start, end, force ->
            viewModel.getReports(clubId, memberIds, start, end, force)
        },
        onGetTransactions = { clubId, memberId, start, end, force ->
            viewModel.getTransactions(clubId, memberId, start, end, force)
        },
        role = "admin"
    )
}
