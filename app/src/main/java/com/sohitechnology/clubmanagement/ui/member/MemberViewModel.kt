package com.sohitechnology.clubmanagement.ui.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import com.sohitechnology.clubmanagement.data.model.AddMemberRequest
import com.sohitechnology.clubmanagement.data.model.MemberRequest
import com.sohitechnology.clubmanagement.data.model.UpdateMemberRequest
import com.sohitechnology.clubmanagement.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val repository: MemberRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(MemberState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MemberUiEvent>()
    val event = _event.asSharedFlow()

    private var lastClubId: String = "0"
    private var lastStatus: Int = 0

    fun loadMembers(clubId: String, status: Int, forceRefresh: Boolean) {
        lastClubId = clubId
        lastStatus = status
        
        if (!forceRefresh && _state.value.members.isNotEmpty()) return

        viewModelScope.launch {
            val companyId = dataStore.readOnce(
                SessionKeys.COMPANY_ID,
                "0"
            )
            repository.getMembers(
                MemberRequest(
                    cId = companyId.toInt(),
                    clubId = clubId,
                    status = status
                ),
                forceRefresh
            ).collect { result ->
                when (result) {
                    ApiResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is ApiResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                members = result.data.map { dto -> dto.toUi() }
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

    fun refreshMembers() {
        loadMembers(lastClubId, lastStatus, true)
    }

    fun selectMember(member: MemberUiModel) {
        _state.update { it.copy(selectedMember = member) }
    }

    fun updateMember(request: UpdateMemberRequest) {
        viewModelScope.launch {
            repository.updateMember(request).collect { result ->
                when (result) {
                    ApiResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is ApiResult.Success -> {
                        if (result.data.success == true){
                            _state.update { it.copy(isLoading = false) }
                            _event.emit(MemberUiEvent.UpdateSuccess(result.data.message ?: "Update successful"))
                        } else{
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.data.message ?: "Update failed"
                                )
                            }
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

    fun addMember(request: AddMemberRequest) {
        viewModelScope.launch {
            repository.addMember(request).collect { result ->
                when (result) {
                    ApiResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is ApiResult.Success -> {
                        if (result.data.success == true) {
                            _state.update { it.copy(isLoading = false) }
                            _event.emit(MemberUiEvent.AddSuccess(result.data.message ?: "Member added successfully"))
                        } else {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.data.message ?: "Add member failed"
                                )
                            }
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

sealed class MemberUiEvent {
    data class UpdateSuccess(val message: String) : MemberUiEvent()
    data class AddSuccess(val message: String) : MemberUiEvent()
}
