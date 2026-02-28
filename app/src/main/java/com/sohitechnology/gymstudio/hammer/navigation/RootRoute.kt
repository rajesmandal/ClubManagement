package com.sohitechnology.gymstudio.hammer.navigation

sealed class RootRoute(val route: String) {
    object Auth : RootRoute("auth")
    object Main : RootRoute("main")
}
