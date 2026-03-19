package com.sohitechnology.gymstudio.hammer.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.gymstudio.hammer.core.common.ApiResult
import com.sohitechnology.gymstudio.hammer.core.session.AppDataStore
import com.sohitechnology.gymstudio.hammer.core.session.SessionKeys
import com.sohitechnology.gymstudio.hammer.data.model.LogoutRequest
import com.sohitechnology.gymstudio.hammer.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _themeMode = MutableStateFlow("system")
    val themeMode = _themeMode.asStateFlow()

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked = _isAppLocked.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = dataStore.read(SessionKeys.IS_LOGGED_IN, false)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isAppLockEnabled: StateFlow<Boolean> = dataStore.read(SessionKeys.IS_APP_LOCK_ENABLED, false)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    var isLogoutLoading by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            _themeMode.value = dataStore.readOnce(SessionKeys.THEME_MODE, "system")
            // Initialize app lock state if enabled
            if (dataStore.readOnce(SessionKeys.IS_APP_LOCK_ENABLED, false)) {
                _isAppLocked.value = true
            }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            _themeMode.value = mode
            dataStore.save(SessionKeys.THEME_MODE, mode)
        }
    }

    fun setAppLocked(locked: Boolean) {
        _isAppLocked.value = locked
    }

    fun logout() {
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            val userId = dataStore.readOnce(SessionKeys.USER_ID, 0)

            authRepository.logout(LogoutRequest(cId = companyId, userId = userId)).collect { result ->
                when (result) {
                    is ApiResult.Loading -> isLogoutLoading = true
                    is ApiResult.Success -> {
                        isLogoutLoading = false
                        dataStore.clear()
                        com.sohitechnology.gymstudio.hammer.core.NavigationManager.navigate(com.sohitechnology.gymstudio.hammer.core.NavigationEvent.ToLogin)
                    }
                    is ApiResult.Error -> {
                        isLogoutLoading = false
                        // Even if API fails, clear local data and navigate to login
                        dataStore.clear()
                        com.sohitechnology.gymstudio.hammer.core.NavigationManager.navigate(com.sohitechnology.gymstudio.hammer.core.NavigationEvent.ToLogin)
                    }
                }
            }
        }
    }
}
