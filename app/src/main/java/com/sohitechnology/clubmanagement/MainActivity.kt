package com.sohitechnology.clubmanagement

import android.os.Bundle
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.sohitechnology.clubmanagement.core.NavigationEvent
import com.sohitechnology.clubmanagement.core.NavigationManager
import com.sohitechnology.clubmanagement.main.MainViewModel
import com.sohitechnology.clubmanagement.navigation.AuthRoute
import com.sohitechnology.clubmanagement.navigation.RootNavGraph
import com.sohitechnology.clubmanagement.navigation.RootRoute
import com.sohitechnology.clubmanagement.ui.auth.BiometricAuthenticator
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val TAG = "MainActivityAuth"
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var biometricAuthenticator: BiometricAuthenticator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isSystemDark = isSystemInDarkTheme()
            
            val isDarkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemDark
            }

            DisposableEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDarkTheme) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    },
                    navigationBarStyle = if (isDarkTheme) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    }
                )
                onDispose {}
            }

            ClubManagementTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isAppLocked by viewModel.isAppLocked.collectAsState()
                    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsState()

                    val navController = rememberNavController()

                    LaunchedEffect(Unit) {
                        NavigationManager.events.collect { event ->
                            when (event) {
                                NavigationEvent.ToHome -> {
                                    navController.navigate(RootRoute.Main.route) {
                                        popUpTo(RootRoute.Auth.route) { inclusive = true }
                                    }
                                }
                                NavigationEvent.ToLogin -> {
                                    navController.navigate(AuthRoute.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        }
                    }

                    // Main App Content
                    RootNavGraph(
                        navController = navController,
                        biometricAuthenticator = biometricAuthenticator
                    )

                    // App Lock Overlay - Only show if LOGGED IN, LOCK ENABLED, and APP IS LOCKED (Cold Start)
                    if (isLoggedIn == true && isAppLockEnabled && isAppLocked) {
                        // Automatically trigger biometric on first composition (Cold Start)
                        LaunchedEffect(Unit) {
                            showBiometricPrompt()
                        }

                        AppLockOverlay(
                            onUnlockClick = { showBiometricPrompt() }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AppLockOverlay(onUnlockClick: () -> Unit) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                BackHandler(enabled = true) {
                    moveTaskToBack(true)
                }

                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "App is Locked",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Authentication is required to access this app.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = onUnlockClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Unlock Now", fontSize = 16.sp)
                    }
                }
            }
        }
    }

    private fun showBiometricPrompt() {
        val authStatus = biometricAuthenticator.canAuthenticate()
        if (authStatus == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricAuthenticator.promptBiometric(
                activity = this,
                title = "App Locked",
                subtitle = "Authenticate to continue",
                onSuccess = {
                    viewModel.setAppLocked(false)
                },
                onError = { errorCode, _ ->
                    // Handle specific errors if needed, overlay remains visible
                },
                onFailed = {
                    // Failures keep the overlay visible
                }
            )
        } else {
            // No security enrolled, just unlock
            viewModel.setAppLocked(false)
        }
    }
}
