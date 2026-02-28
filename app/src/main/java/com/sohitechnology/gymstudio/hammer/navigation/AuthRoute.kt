package com.sohitechnology.gymstudio.hammer.navigation

sealed class AuthRoute(val route: String) {
    object Splash : AuthRoute("splash")
    object Login : AuthRoute("login")
}
