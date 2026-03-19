package com.sohitechnology.gymstudio.hammer.auth.login

import com.sohitechnology.gymstudio.hammer.ui.UiMessage

data class LoginState(
    val companyId: String = "",
    val username: String = "",
    val password: String = "",
    val isAdmin: Boolean = true, // Default to Admin
    val companyIdError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val uiMessage: UiMessage? = null // popup message
) {
    val isLoginEnabled: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && !isLoading && companyId.isNotBlank()
}