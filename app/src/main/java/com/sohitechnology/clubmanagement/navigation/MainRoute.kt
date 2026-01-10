package com.sohitechnology.clubmanagement.navigation

sealed class MainRoute(val route: String) {
    object Home : MainRoute("home")
    object Members : MainRoute("members")
    object Report : MainRoute("report")
    object Profile : MainRoute("profile")
}