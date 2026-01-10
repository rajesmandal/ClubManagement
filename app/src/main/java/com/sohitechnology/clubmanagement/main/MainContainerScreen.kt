package com.sohitechnology.clubmanagement.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sohitechnology.clubmanagement.navigation.AppBottomBar
import com.sohitechnology.clubmanagement.navigation.MainRoute
import com.sohitechnology.clubmanagement.navigation.mainNavGraph

@Composable
fun MainContainerScreen() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomBar(navController) // bottom bar yahin
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = MainRoute.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            mainNavGraph()
        }
    }
}
