package com.sohitechnology.clubmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sohitechnology.clubmanagement.core.NavigationEvent
import com.sohitechnology.clubmanagement.core.NavigationManager
import com.sohitechnology.clubmanagement.navigation.RootNavGraph
import com.sohitechnology.clubmanagement.navigation.RootRoute
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClubManagementTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // global navigation listener
                    LaunchedEffect(Unit) {
                        NavigationManager.events.collect { event ->
                            when (event) {

                                NavigationEvent.ToHome -> {
                                    navController.navigate(RootRoute.Main.route) {
                                        popUpTo(RootRoute.Auth.route) { inclusive = true }
                                    }
                                }

                                NavigationEvent.ToLogin -> {
                                    navController.navigate(RootRoute.Auth.route) {
                                        popUpTo(0)
                                    }
                                }
                            }
                        }
                    }

                    RootNavGraph(navController)
                }
            }
        }

    }
}