package com.sohitechnology.clubmanagement.ui.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import com.sohitechnology.clubmanagement.data.model.MemberRequest
import com.sohitechnology.clubmanagement.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    fun loadMembers(clubId: String, status: Int, forceRefresh: Boolean) {
        // Agar forceRefresh false hai aur data pehle se hai, toh return kar jayein
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

        // Agar refresh button dabaye tab bhi static data hi dikhana hai to:
        //loadStaticMembers(forceRefresh)
    }

    private fun loadStaticMembers(fore: Boolean) {
        _state.update { it.copy(isLoading = true) }

        // Dummy List banayein
        val dummyMembers = listOf(
            MemberUiModel(
                memberId = "1",
                name = "Rahul Sharma",
                userName = "rahul",
                image = "Admin",
                status = "active",
                clubName = "Club Name"
            ),
            MemberUiModel(
                memberId = "2",
                name = "Rajesh Sharma",
                userName = "rajesh",
                image = "Admin",
                status = "deActived",
                clubName = "Club Name"
            ),
            MemberUiModel(
                memberId = "3",
                name = "Pankaj Sharma",
                userName = "pankaj",
                image = "Admin",
                status = "expired",
                clubName = "Club Name"
            ),
            MemberUiModel(
                memberId = "7",
                name = "Ankur Pathak",
                userName = "ankur",
                image = "Admin",
                status = "active",
                clubName = "Club Name"
            ),
        )

        _state.update {
            it.copy(
                isLoading = false, members = dummyMembers, error = null
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

}
