package com.sohitechnology.clubmanagement.navigation

sealed class MainRoute(val route: String) {
    object Home : MainRoute("home")
    object Members : MainRoute("members")
    object MemberDetail : MainRoute("member_detail")
    object AddMember : MainRoute("add_member")
    object PackageSelection : MainRoute("package_selection")
    object Report : MainRoute("report")
    object Profile : MainRoute("profile")
    object MemberGraph : MainRoute("member_graph")
}