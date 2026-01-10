package com.sohitechnology.clubmanagement.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sohitechnology.clubmanagement.auth.login.LoginScreen
import com.sohitechnology.clubmanagement.splash.SplashScreen

fun NavGraphBuilder.authNavGraph() {

    composable(AuthRoute.Splash.route) {
        SplashScreen()
    }

    composable(AuthRoute.Login.route) {
        LoginScreen()
    }
}
