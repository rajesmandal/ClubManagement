package com.sohitechnology.clubmanagement.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import com.sohitechnology.clubmanagement.data.model.MemberCountData
import com.sohitechnology.clubmanagement.data.model.MemberCountRequest
import com.sohitechnology.clubmanagement.data.model.MemberExpiryRequest
import com.sohitechnology.clubmanagement.data.repository.HomeRepository
import com.sohitechnology.clubmanagement.ui.member.MemberUiModel
import com.sohitechnology.clubmanagement.ui.member.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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

    fun getMemberCount() {
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            homeRepository.getMemberCount(MemberCountRequest(companyId)).collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _isLoading.value = true
                        _error.value = null
                    }
                    is ApiResult.Success -> {
                        _isLoading.value = false
                        _memberCount.value = result.data.data
                    }
                    is ApiResult.Error -> {
                        _isLoading.value = false
                        _error.value = result.message
                    }
                }
            }
        }
    }

    fun getMemberExpiry() {
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            homeRepository.getMemberExpiry(MemberExpiryRequest(companyId)).collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _isExpiryLoading.value = true
                    }
                    is ApiResult.Success -> {
                        _isExpiryLoading.value = false
                        _expiryMembers.value = (result.data.data ?: emptyList()).map { dto ->
                            // Map Data class to MemberUiModel
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
                    }
                    is ApiResult.Error -> {
                        _isExpiryLoading.value = false
                    }
                }
            }
        }
    }
}
