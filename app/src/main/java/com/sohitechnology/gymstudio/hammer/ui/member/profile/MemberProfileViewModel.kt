package com.sohitechnology.gymstudio.hammer.ui.member.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.gymstudio.hammer.core.common.ApiResult
import com.sohitechnology.gymstudio.hammer.core.session.AppDataStore
import com.sohitechnology.gymstudio.hammer.core.session.SessionKeys
import com.sohitechnology.gymstudio.hammer.data.model.MemberDetailRequest
import com.sohitechnology.gymstudio.hammer.data.repository.AuthRepository
import com.sohitechnology.gymstudio.hammer.data.repository.HomeRepository
import com.sohitechnology.gymstudio.hammer.ui.profile.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val homeRepository: HomeRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile = _userProfile.asStateFlow()

    val isAppLockEnabled: StateFlow<Boolean> = dataStore.read(SessionKeys.IS_APP_LOCK_ENABLED, false)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    var isLoading by mutableStateOf(false)
        private set

    var logoutSuccess by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        fetchMemberProfile()
    }

    private fun fetchMemberProfile() {
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            val userId = dataStore.readOnce(SessionKeys.USER_ID, 0)

            if (companyId != 0 && userId != 0) {
                homeRepository.getMemberById(MemberDetailRequest(cId = companyId, id = userId)).collect { result ->
                    when (result) {
                        is ApiResult.Loading -> isLoading = true
                        is ApiResult.Success -> {
                            isLoading = false
                            val data = result.data.data?.firstOrNull()
                            _userProfile.update {
                                it.copy(
                                    fullName = data?.name ?: "",
                                    userName = data?.userName ?: "",
                                    role = "Member",
                                    profileImage = data?.image ?: "",
                                    email = data?.emailId ?: "",
                                    contactNo = data?.contactNo ?: "",
                                    address = data?.address ?: "",
                                    companyName = data?.clubName ?: ""
                                )
                            }
                        }
                        is ApiResult.Error -> {
                            isLoading = false
                            error = result.message
                        }
                    }
                }
            }
        }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.save(SessionKeys.IS_APP_LOCK_ENABLED, enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            isLoading = true
            val companyId = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            authRepository.logout(com.sohitechnology.gymstudio.hammer.data.model.LogoutRequest(companyId.toIntOrNull() ?: 0)).collect { result ->
                when (result) {
                    is ApiResult.Success, is ApiResult.Error -> {
                        dataStore.clear()
                        logoutSuccess = true
                    }
                    is ApiResult.Loading -> isLoading = true
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
