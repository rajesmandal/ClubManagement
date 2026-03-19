package com.sohitechnology.gymstudio.hammer.ui.member.profile

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sohitechnology.gymstudio.hammer.ui.auth.BiometricAuthenticator
import com.sohitechnology.gymstudio.hammer.ui.profile.ProfileContent

@Composable
fun MemberProfileScreen(
    navController: NavHostController,
    onMenuClick: () -> Unit = {},
    viewModel: MemberProfileViewModel = hiltViewModel(),
    biometricAuthenticator: BiometricAuthenticator
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsState()

    LaunchedEffect(viewModel.logoutSuccess) {
        if (viewModel.logoutSuccess) {
            com.sohitechnology.gymstudio.hammer.core.NavigationManager.navigate(com.sohitechnology.gymstudio.hammer.core.NavigationEvent.ToLogin)
        }
    }

    ProfileContent(
        navController = navController,
        userProfile = userProfile,
        isAppLockEnabled = isAppLockEnabled,
        isNotificationEnabled = true,
        isLoading = viewModel.isLoading,
        error = viewModel.error,
        onMenuClick = onMenuClick,
        onNotificationToggle = { },
        onAppLockToggle = { checked ->
            viewModel.setAppLockEnabled(checked)
        },
        onUpdateCredentials = { _, _, _ -> }, // Members might not have this or it's different
        onUpdateProfile = { _, _, _, _, _, _ -> }, // Read-only for members in this version maybe?
        onUploadImage = { },
        onLogout = { viewModel.logout() },
        onClearError = { viewModel.clearError() },
        showSuccessPopup = false,
        successMessage = "",
        onDismissSuccessPopup = { },
        role = "member"
    )
}
