package com.sohitechnology.clubmanagement.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sohitechnology.clubmanagement.main.MainContainerScreen
import com.sohitechnology.clubmanagement.ui.auth.BiometricAuthenticator

@Composable
fun RootNavGraph(
    navController: NavHostController,
    biometricAuthenticator: BiometricAuthenticator
) {

    NavHost(
        navController = navController,
        startDestination = RootRoute.Auth.route // app start
    ) {

        navigation(
            route = RootRoute.Auth.route,
            startDestination = AuthRoute.Splash.route
        ) {
            authNavGraph()  // auth means splash and login
        }

        composable(RootRoute.Main.route) {
            MainContainerScreen(biometricAuthenticator = biometricAuthenticator)
        }
    }
}
