package com.sohitechnology.gymstudio.hammer.ui.admin.profile

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sohitechnology.gymstudio.hammer.ui.auth.BiometricAuthenticator
import com.sohitechnology.gymstudio.hammer.ui.profile.ProfileContent

@Composable
fun AdminProfileScreen(
    navController: NavHostController,
    onMenuClick: () -> Unit = {},
    viewModel: AdminProfileViewModel = hiltViewModel(),
    biometricAuthenticator: BiometricAuthenticator
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsState()
    
    var showSuccessPopup by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.credentialUpdateSuccess.collect { message ->
            successMessage = message
            showSuccessPopup = true
            // In a real app, you might want a delay before logout
            viewModel.logout()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.profileUpdateSuccess.collect { message ->
            successMessage = message
            showSuccessPopup = true
        }
    }

    LaunchedEffect(viewModel.logoutSuccess) {
        if (viewModel.logoutSuccess) {
            com.sohitechnology.gymstudio.hammer.core.NavigationManager.navigate(com.sohitechnology.gymstudio.hammer.core.NavigationEvent.ToLogin)
        }
    }

    ProfileContent(
        navController = navController,
        userProfile = userProfile,
        isAppLockEnabled = isAppLockEnabled,
        isNotificationEnabled = true, // Simplified
        isLoading = viewModel.isLoading,
        error = viewModel.error,
        onMenuClick = onMenuClick,
        onNotificationToggle = { },
        onAppLockToggle = { checked ->
            if (checked) {
                // Biometric logic would go here
                viewModel.setAppLockEnabled(true)
            } else {
                viewModel.setAppLockEnabled(false)
            }
        },
        onUpdateCredentials = { password, userName, type ->
            viewModel.updateCredentials(password, userName, type)
        },
        onUpdateProfile = { name, email, contact, address, country, company ->
            viewModel.updateProfile(name, email, contact, address, country, company)
        },
        onUploadImage = { viewModel.uploadProfileImage(it) },
        onLogout = { viewModel.logout() },
        onClearError = { viewModel.clearError() },
        showSuccessPopup = showSuccessPopup,
        successMessage = successMessage,
        onDismissSuccessPopup = { showSuccessPopup = false },
        role = userProfile.role
    )
}
