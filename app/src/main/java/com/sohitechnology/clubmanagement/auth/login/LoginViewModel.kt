package com.sohitechnology.clubmanagement.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel(){
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.value,
                        emailError = null,
                        successMessage = null
                    )
                }
            }

            is LoginEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.value,
                        passwordError = null,
                        successMessage = null
                    )
                }
            }

            LoginEvent.LoginClicked -> {
                validateAndLogin()
            }
        }
    }

    private fun validateAndLogin() {
        val current = _state.value

        when {
            current.email.isBlank() -> {
                _state.update {
                    it.copy(
                        emailError = "Email is required",
                        passwordError = null
                    )
                }
            }

            !Patterns.EMAIL_ADDRESS.matcher(current.email).matches() -> {
                _state.update {
                    it.copy(
                        emailError = "Enter a valid email address",
                        passwordError = null
                    )
                }
            }

            current.password.isBlank() -> {
                _state.update {
                    it.copy(
                        passwordError = "Password is required",
                        emailError = null
                    )
                }
            }

            current.password.length < 6 -> {
                _state.update {
                    it.copy(
                        passwordError = "Password must be at least 6 characters",
                        emailError = null
                    )
                }
            }

            else -> {
                _state.update {
                    it.copy(
                        emailError = null,
                        passwordError = null,
                        successMessage = "Login successful"
                    )
                }
            }
        }
    }

}