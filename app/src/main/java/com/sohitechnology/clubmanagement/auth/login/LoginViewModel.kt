package com.sohitechnology.clubmanagement.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.NavigationEvent
import com.sohitechnology.clubmanagement.core.NavigationManager
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import com.sohitechnology.clubmanagement.data.model.AuthRepository
import com.sohitechnology.clubmanagement.data.model.LoginData
import com.sohitechnology.clubmanagement.data.model.LoginRequest
import com.sohitechnology.clubmanagement.ui.UiMessage
import com.sohitechnology.clubmanagement.ui.UiMessageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.trim

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository, // api repo
    private val dataStore: AppDataStore      // session store
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {

            is LoginEvent.CompanyIdChanged -> {
                _state.update {
                    it.copy(
                        companyId = event.value,
                        companyIdError = null
                    )
                }
            }

            is LoginEvent.EmailChanged -> {
                _state.update {
                    it.copy(
                        username = event.value,
                        usernameError = null
                    )
                }
            }

            is LoginEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.value,
                        passwordError = null
                    )
                }
            }

            LoginEvent.LoginClicked -> {
                validateAndLogin() // trigger login flow
            }
        }
    }

    private fun validateAndLogin() {
        val current = _state.value

        when {

            !current.companyId.trim().all { it.isDigit() } ->{
                _state.update { it.copy(companyIdError = "Company ID must contain only numbers") }
            }

            current.username.isBlank() -> {
                _state.update {
                    it.copy(
                        usernameError = "Enter a valid username"
                    )
                }
            }

            current.password.isBlank() -> {
                _state.update { it.copy(passwordError = "Password required") }
            }

            else -> {
                loginApiCall() // validation pass → api call
            }
        }
    }

    private fun loginApiCall() {
        viewModelScope.launch {
            repository.login(
                LoginRequest(
                    userName = state.value.username.trim(),
                    password = state.value.password,
                    cId = state.value.companyId.trim().toInt(),
                    deviceId = ""
                )
            ).collect { result ->

                when (result) {

                    ApiResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }

                    is ApiResult.Success -> {
                        val response = result.data
                        val data = response.data

                        if (response.success && data != null) {
                            saveSession(data)
                            _state.update { it.copy(isLoading = false, successMessage = "Login Successfully") }
                            // login success pe
                            NavigationManager.navigate(NavigationEvent.ToHome)
                        } else {
                            // Agar success false hai ya data null hai
                            val errorMsg = response.message.ifEmpty { "Unknown Reason" }

                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    uiMessage = UiMessage(
                                        title = "Login Failed",
                                        message = errorMsg,
                                        type = UiMessageType.INFO
                                    )
                                )
                            }
                        }
                    }

                    is ApiResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                uiMessage = UiMessage(
                                    title = "Login Failed",
                                    message = result.message,
                                    type = UiMessageType.ERROR
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun saveSession(data: LoginData) { //after login save details
        dataStore.save(SessionKeys.TOKEN, data.accessToken) // token
        dataStore.save(SessionKeys.USER_ID, data.userId) // userid
        dataStore.save(SessionKeys.IS_LOGGED_IN, true)
        dataStore.save(SessionKeys.COMPANY_ID, data.cId.toString())
    }

    fun clearUiMessage() {
        _state.update { it.copy(uiMessage = null) } // dismiss popup
    }
}
