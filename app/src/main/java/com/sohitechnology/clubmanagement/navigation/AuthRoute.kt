package com.sohitechnology.clubmanagement.navigation

sealed class AuthRoute(val route: String) {
    object Splash : AuthRoute("splash")
    object Login : AuthRoute("login")
}
