package com.sohitechnology.clubmanagement.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import com.sohitechnology.clubmanagement.data.model.AuthRepository
import com.sohitechnology.clubmanagement.data.model.LogoutRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: AppDataStore,
    private val authRepository: AuthRepository
) : ViewModel() {

    val themeMode: StateFlow<String> = dataStore.read(SessionKeys.THEME_MODE, "system")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val isLoggedIn: StateFlow<Boolean?> = dataStore.read(SessionKeys.IS_LOGGED_IN, false)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isAppLockEnabled: StateFlow<Boolean> = dataStore.read(SessionKeys.IS_APP_LOCK_ENABLED, false)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Initial state is true. On cold start, this stays true.
    private val _isAppLocked = MutableStateFlow(true)
    val isAppLocked = _isAppLocked.asStateFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    var isLogoutLoading by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            // Monitor login state transitions
            isLoggedIn.collect { loggedIn ->
                if (loggedIn == false) {
                    _isAppLocked.value = false
                }
            }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            dataStore.save(SessionKeys.THEME_MODE, mode)
        }
    }

    fun setAppLocked(locked: Boolean) {
        _isAppLocked.value = locked
    }

    fun logout() {
        viewModelScope.launch {
            isLogoutLoading = true
            val companyId = dataStore.read(SessionKeys.COMPANY_ID, "").first()
            authRepository.logout(LogoutRequest(companyId.toIntOrNull() ?: 0)).collect { result ->
                when (result) {
                    is ApiResult.Success, is ApiResult.Error -> {
                        dataStore.clear()
                        isLogoutLoading = false
                        setAppLocked(false)
                        _logoutEvent.emit(Unit)
                    }
                    is ApiResult.Loading -> {
                        isLogoutLoading = true
                    }
                }
            }
        }
    }
}
