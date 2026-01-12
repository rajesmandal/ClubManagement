package com.sohitechnology.clubmanagement.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sohitechnology.clubmanagement.ui.member.MembersScreen
import com.sohitechnology.clubmanagement.ui.profile.ProfileScreen
import com.sohitechnology.clubmanagement.ui.ReportScreen
import com.sohitechnology.clubmanagement.ui.home.HomeScreen

fun NavGraphBuilder.mainNavGraph() {

    composable(MainRoute.Home.route) {
        HomeScreen()
    }

    composable(MainRoute.Members.route) {
        MembersScreen()
    }

    composable(MainRoute.Report.route) {
        ReportScreen()
    }

    composable(MainRoute.Profile.route) {
        ProfileScreen()
    }
}
