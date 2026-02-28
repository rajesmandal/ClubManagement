package com.sohitechnology.gymstudio.hammer.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sohitechnology.gymstudio.hammer.auth.login.LoginScreen
import com.sohitechnology.gymstudio.hammer.splash.SplashScreen

fun NavGraphBuilder.authNavGraph() {

    composable(AuthRoute.Splash.route) {
        SplashScreen()
    }

    composable(AuthRoute.Login.route) {
        LoginScreen()
    }
}
