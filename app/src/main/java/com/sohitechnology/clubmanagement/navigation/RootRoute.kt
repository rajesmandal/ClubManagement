package com.sohitechnology.clubmanagement.navigation

sealed class RootRoute(val route: String) {
    object Auth : RootRoute("auth")
    object Main : RootRoute("main")
}
