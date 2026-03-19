package com.sohitechnology.gymstudio.hammer.ui.admin.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.sohitechnology.gymstudio.hammer.core.common.ApiResult
import com.sohitechnology.gymstudio.hammer.core.session.AppDataStore
import com.sohitechnology.gymstudio.hammer.core.session.SessionKeys
import com.sohitechnology.gymstudio.hammer.data.cache.HomeCache
import com.sohitechnology.gymstudio.hammer.data.model.MemberCountData
import com.sohitechnology.gymstudio.hammer.data.model.MemberCountRequest
import com.sohitechnology.gymstudio.hammer.data.model.MemberExpiryRequest
import com.sohitechnology.gymstudio.hammer.data.model.UpdateFcmTokenRequest
import com.sohitechnology.gymstudio.hammer.data.repository.HomeRepository
import com.sohitechnology.gymstudio.hammer.ui.member.MemberUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _memberCount = MutableStateFlow<MemberCountData?>(null)
    val memberCount = _memberCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _expiryMembers = MutableStateFlow<List<MemberUiModel>>(emptyList())
    val expiryMembers = _expiryMembers.asStateFlow()

    private val _isExpiryLoading = MutableStateFlow(false)
    val isExpiryLoading = _isExpiryLoading.asStateFlow()

    init {
        viewModelScope.launch {
            val role = dataStore.readOnce(SessionKeys.ROLE, "").lowercase()
            if (role != "member" && role.isNotEmpty()) {
                loadInitialData()
                updateFcmToken()
            }
        }
    }

    private fun loadInitialData() {
        // Load from cache initially if available
        HomeCache.memberCount?.let { _memberCount.value = it }
        HomeCache.expiryMembers?.let { _expiryMembers.value = it }

        getMemberCount()
        getMemberExpiry()
    }

    private fun updateFcmToken() {
        viewModelScope.launch {
            try {
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
                val companyId = companyIdStr.toIntOrNull() ?: 0
                val userId = dataStore.readOnce(SessionKeys.USER_ID, 0)

                if (fcmToken.isNotEmpty() && userId != 0 && companyId != 0) {
                    homeRepository.updateFcmToken(
                        UpdateFcmTokenRequest(
                            cId = companyId,
                            fcmToken = fcmToken,
                            userId = userId
                        )
                    ).collect { /* No action needed for response */ }
                }
            } catch (e: Exception) {
                // Silently fail for token update
            }
        }
    }

    fun getMemberCount(forceRefresh: Boolean = false) {
        if (!forceRefresh && HomeCache.memberCount != null) {
            _memberCount.value = HomeCache.memberCount
            return
        }

        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            if (companyId == 0) return@launch
            
            homeRepository.getMemberCount(MemberCountRequest(companyId)).collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _isLoading.value = true
                        _error.value = null
                    }
                    is ApiResult.Success -> {
                        _isLoading.value = false
                        val data = result.data.data
                        _memberCount.value = data
                        HomeCache.memberCount = data
                    }
                    is ApiResult.Error -> {
                        _isLoading.value = false
                        _error.value = result.message
                    }
                }
            }
        }
    }

    fun getMemberExpiry(forceRefresh: Boolean = false) {
        if (!forceRefresh && HomeCache.expiryMembers != null) {
            _expiryMembers.value = HomeCache.expiryMembers!!
            return
        }

        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            if (companyId == 0) return@launch

            homeRepository.getMemberExpiry(MemberExpiryRequest(companyId)).collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _isExpiryLoading.value = true
                    }
                    is ApiResult.Success -> {
                        _isExpiryLoading.value = false
                        val mappedList = (result.data.data ?: emptyList()).map { dto ->
                            MemberUiModel(
                                id = dto.id ?: 0,
                                memberId = dto.memberId ?: "",
                                name = dto.name ?: "",
                                userName = dto.userName ?: "",
                                password = dto.password ?: "",
                                image = dto.image ?: "",
                                status = dto.status ?: "Unknown",
                                gender = dto.gender ?: "",
                                contactNo = dto.contactNo ?: "",
                                emailId = dto.emailId ?: "",
                                clubName = dto.clubName ?: "",
                                clubId = dto.clubId ?: 0,
                                birthDay = dto.birthDay ?: "",
                                hireDay = dto.hireDay ?: "",
                                address = dto.address ?: "",
                                nationality = dto.nationality ?: "",
                                startDate = dto.startDate ?: "",
                                expiryDate = dto.expiryDate ?: ""
                            )
                        }
                        _expiryMembers.value = mappedList
                        HomeCache.expiryMembers = mappedList
                    }
                    is ApiResult.Error -> {
                        _isExpiryLoading.value = false
                    }
                }
            }
        }
    }

    fun reloadAll() {
        getMemberCount(forceRefresh = true)
        getMemberExpiry(forceRefresh = true)
    }
}
