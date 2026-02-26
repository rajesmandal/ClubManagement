package com.sohitechnology.clubmanagement.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import com.sohitechnology.clubmanagement.data.model.AuthRepository
import com.sohitechnology.clubmanagement.data.model.CredentialUpdateRequest
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfile(
    val fullName: String = "",
    val userName: String = "",
    val role: String = "",
    val profileImage: String = "",
    val companyId: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile = _userProfile.asStateFlow()

    private val _credentialUpdateSuccess = MutableSharedFlow<String>()
    val credentialUpdateSuccess = _credentialUpdateSuccess.asSharedFlow()

    val isAppLockEnabled: StateFlow<Boolean> = dataStore.read(SessionKeys.IS_APP_LOCK_ENABLED, false)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    var isLoading by mutableStateOf(false)
        private set

    var logoutSuccess by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val fullName = dataStore.readOnce(SessionKeys.FULL_NAME, "")
            val userName = dataStore.readOnce(SessionKeys.USER_NAME, "")
            val role = dataStore.readOnce(SessionKeys.ROLE, "")
            val profileImage = dataStore.readOnce(SessionKeys.PROFILE_IMAGE, "")
            val companyId = dataStore.readOnce(SessionKeys.COMPANY_ID, "")

            _userProfile.update {
                it.copy(
                    fullName = fullName,
                    userName = userName,
                    role = role,
                    profileImage = profileImage,
                    companyId = companyId
                )
            }
        }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.save(SessionKeys.IS_APP_LOCK_ENABLED, enabled)
        }
    }

    fun updateCredentials(password: String, newUserName: String, updateType: Int) {
        viewModelScope.launch {
            isLoading = true
            error = null
            val companyId = dataStore.readOnce(SessionKeys.COMPANY_ID, "0").toIntOrNull() ?: 0
            val userId = dataStore.readOnce(SessionKeys.USER_ID, 0)

            val request = CredentialUpdateRequest(
                cId = companyId,
                id = userId,
                password = if (updateType == 1) "" else password,
                userName = if (updateType == 0) "" else newUserName,
                updateType = updateType
            )

            authRepository.updateCredential(request).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        if (result.data.success == true) {
                            _credentialUpdateSuccess.emit(result.data.message ?: "Credentials updated successfully. Logging out...")
                        } else {
                            error = result.data.message ?: "Failed to update credentials"
                        }
                    }
                    is ApiResult.Error -> {
                        error = result.message ?: "An error occurred"
                    }
                    is ApiResult.Loading -> {
                        isLoading = true
                    }
                }
            }
            isLoading = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            isLoading = true
            val companyId = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            
            authRepository.logout(LogoutRequest(companyId.toIntOrNull() ?: 0)).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        dataStore.clear()
                        logoutSuccess = true
                    }
                    is ApiResult.Error -> {
                        dataStore.clear()
                        logoutSuccess = true
                    }
                    is ApiResult.Loading -> {
                        isLoading = true
                    }
                }
            }
            isLoading = false
        }
    }
    
    fun resetLogoutState() {
        logoutSuccess = false
    }

    fun clearError() {
        error = null
    }
}
