package com.sohitechnology.clubmanagement.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sohitechnology.clubmanagement.ui.member.MembersScreen
import com.sohitechnology.clubmanagement.ui.profile.ProfileScreen
import com.sohitechnology.clubmanagement.ui.home.HomeScreen
import com.sohitechnology.clubmanagement.ui.member.AddMemberScreen
import com.sohitechnology.clubmanagement.ui.member.MemberDetailScreen
import com.sohitechnology.clubmanagement.ui.member.MemberViewModel
import com.sohitechnology.clubmanagement.ui.member.PackageSelectionScreen
import com.sohitechnology.clubmanagement.ui.member.PackageViewModel
import com.sohitechnology.clubmanagement.ui.report.ReportTabScreen

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    onMenuClick: () -> Unit
) {

    navigation(
        route = MainRoute.MemberGraph.route,
        startDestination = MainRoute.Home.route
    ) {
        composable(MainRoute.Home.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(MainRoute.MemberGraph.route)
            }
            val memberViewModel: MemberViewModel = hiltViewModel(parentEntry)

            HomeScreen(
                navController = navController,
                onMenuClick = onMenuClick,
                onMemberClick = { member ->
                    memberViewModel.selectMember(member)
                    navController.navigate(MainRoute.MemberDetail.route)
                },
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = this@composable
            )
        }

        composable(MainRoute.Members.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(MainRoute.MemberGraph.route)
            }
            val viewModel: MemberViewModel = hiltViewModel(parentEntry)

            MembersScreen(
                navController = navController,
                viewModel = viewModel,
                onMemberClick = { member ->
                    viewModel.selectMember(member)
                    navController.navigate(MainRoute.MemberDetail.route)
                },
                onAddMemberClick = { clubId ->
                    navController.navigate("${MainRoute.AddMember.route}/$clubId")
                },
                onMenuClick = onMenuClick,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = this@composable
            )
        }

        composable(MainRoute.MemberDetail.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(MainRoute.MemberGraph.route)
            }
            val viewModel: MemberViewModel = hiltViewModel(parentEntry)

            MemberDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToMembers = {
                    navController.popBackStack(MainRoute.Members.route, inclusive = false)
                },
                onRenew = { member ->
                    navController.navigate("${MainRoute.PackageSelection.route}/${member.memberId}/${member.expiryDate}")
                },
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = this@composable
            )
        }

        composable("${MainRoute.AddMember.route}/{clubId}") { backStackEntry ->
            val clubId = backStackEntry.arguments?.getString("clubId")?.toInt() ?: 0
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(MainRoute.MemberGraph.route)
            }
            val viewModel: MemberViewModel = hiltViewModel(parentEntry)

            AddMemberScreen(
                viewModel = viewModel,
                clubId = clubId,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.popBackStack(MainRoute.Members.route, inclusive = false)
                }
            )
        }
    }

    composable("${MainRoute.PackageSelection.route}/{memberId}/{expiryDate}") { backStackEntry ->
        val memberId = backStackEntry.arguments?.getString("memberId") ?: ""
        val expiryDate = backStackEntry.arguments?.getString("expiryDate") ?: ""
        val viewModel: PackageViewModel = hiltViewModel()

        PackageSelectionScreen(
            viewModel = viewModel,
            memberId = memberId,
            memberExpiryDate = expiryDate,
            onBack = { navController.popBackStack() },
            onRenewSuccess = {
                navController.popBackStack(MainRoute.MemberDetail.route, inclusive = false)
            }
        )
    }

    composable(MainRoute.Report.route) {
        ReportTabScreen(navController = navController, onMenuClick = onMenuClick)
    }

    composable(MainRoute.Profile.route) {
        ProfileScreen(navController = navController, onMenuClick = onMenuClick)
    }
}
