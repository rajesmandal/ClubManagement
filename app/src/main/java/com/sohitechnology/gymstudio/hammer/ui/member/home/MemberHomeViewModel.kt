package com.sohitechnology.gymstudio.hammer.ui.member.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.sohitechnology.gymstudio.hammer.core.common.ApiResult
import com.sohitechnology.gymstudio.hammer.core.session.AppDataStore
import com.sohitechnology.gymstudio.hammer.core.session.SessionKeys
import com.sohitechnology.gymstudio.hammer.data.model.MemberDetailData
import com.sohitechnology.gymstudio.hammer.data.model.MemberDetailRequest
import com.sohitechnology.gymstudio.hammer.data.model.UpdateFcmTokenRequest
import com.sohitechnology.gymstudio.hammer.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MemberHomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _memberDetail = MutableStateFlow<MemberDetailData?>(null)
    val memberDetail = _memberDetail.asStateFlow()

    private val _isLoading = MutableStateFlow(true) // Start with loading
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        updateMemberFcmToken()
        getMemberDetail()
    }

    private fun updateMemberFcmToken() {
        viewModelScope.launch {
            try {
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
                val companyId = companyIdStr.toIntOrNull() ?: 0
                val userId = dataStore.readOnce(SessionKeys.USER_ID, 0)

                if (fcmToken.isNotEmpty() && userId != 0 && companyId != 0) {
                    homeRepository.memberUpdateFcmToken(
                        UpdateFcmTokenRequest(
                            cId = companyId,
                            fcmToken = fcmToken,
                            userId = userId
                        )
                    ).collect { /* No action needed for response */ }
                }
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    fun getMemberDetail() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            val userId = dataStore.readOnce(SessionKeys.USER_ID, 0)

            if (companyId != 0 && userId != 0) {
                homeRepository.getMemberById(MemberDetailRequest(cId = companyId, id = userId)).collect { result ->
                    when (result) {
                        is ApiResult.Loading -> _isLoading.value = true
                        is ApiResult.Success -> {
                            _isLoading.value = false
                            val memberData = result.data.data?.firstOrNull()
                            _memberDetail.value = memberData
                            if (memberData == null) {
                                _error.value = "Member details not found"
                            }
                            
                            // Save clubId to local storage
                            memberData?.clubId?.let {
                                dataStore.save(SessionKeys.CLUB_ID, it)
                            }
                        }
                        is ApiResult.Error -> {
                            _isLoading.value = false
                            _error.value = result.message
                        }
                    }
                }
            } else {
                _isLoading.value = false
                _error.value = "Session expired. Please login again."
            }
        }
    }
}
