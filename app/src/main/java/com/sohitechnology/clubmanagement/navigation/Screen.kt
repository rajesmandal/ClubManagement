package com.sohitechnology.clubmanagement.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
}