package com.sohitechnology.clubmanagement.ui.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import com.sohitechnology.clubmanagement.data.model.MemberRenewRequest
import com.sohitechnology.clubmanagement.data.model.PackageDto
import com.sohitechnology.clubmanagement.data.model.PackageRequest
import com.sohitechnology.clubmanagement.data.repository.PackageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PackageState(
    val isLoading: Boolean = false,
    val packages: List<PackageDto> = emptyList(),
    val error: String? = null,
    val selectedPackage: PackageDto? = null
)

sealed class PackageUiEvent {
    data class RenewSuccess(val message: String) : PackageUiEvent()
}

@HiltViewModel
class PackageViewModel @Inject constructor(
    private val repository: PackageRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(PackageState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<PackageUiEvent>()
    val event = _event.asSharedFlow()

    init {
        loadPackages()
    }

    private fun loadPackages() {
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            repository.getPackages(PackageRequest(cId = companyId)).collect { result ->
                when (result) {
                    ApiResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is ApiResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                packages = result.data.data ?: emptyList()
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun selectPackage(pkg: PackageDto) {
        _state.update { it.copy(selectedPackage = pkg) }
    }

    fun renewMember(memberId: String) {
        val selectedPkg = _state.value.selectedPackage ?: return
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            
            val request = MemberRenewRequest(
                cId = companyId,
                memberData = memberId,
                planData = (selectedPkg.id ?: 0).toString()
            )
            
            repository.renewMember(request).collect { result ->
                when (result) {
                    ApiResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is ApiResult.Success -> {
                        _state.update { it.copy(isLoading = false) }
                        if (result.data.success == true) {
                            _event.emit(PackageUiEvent.RenewSuccess(result.data.message ?: "Renewed successfully"))
                        } else {
                            _state.update { it.copy(error = result.data.message ?: "Renewal failed") }
                        }
                    }
                    is ApiResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
