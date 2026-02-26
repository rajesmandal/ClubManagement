package com.sohitechnology.clubmanagement.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import com.sohitechnology.clubmanagement.data.model.ReportData
import com.sohitechnology.clubmanagement.data.model.MemberRequest
import com.sohitechnology.clubmanagement.data.model.ReportRequest
import com.sohitechnology.clubmanagement.data.model.TransactionData
import com.sohitechnology.clubmanagement.data.model.TransactionRequest
import com.sohitechnology.clubmanagement.data.repository.MemberRepository
import com.sohitechnology.clubmanagement.data.repository.ReportRepository
import com.sohitechnology.clubmanagement.ui.member.MemberUiModel
import com.sohitechnology.clubmanagement.ui.member.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ReportState(
    val isLoading: Boolean = false,
    val reports: List<ReportData> = emptyList(),
    val transactions: List<TransactionData> = emptyList(),
    val totalAmount: Int = 0,
    val totalCount: Int = 0,
    val members: List<MemberUiModel> = emptyList(),
    val error: String? = null,
    val startDate: String = "",
    val endDate: String = "",
    val selectedClubId: Int = 0,
    val selectedMemberIds: Set<String> = emptySet(),
    val selectedTransactionMemberId: Int = 0
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val memberRepository: MemberRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state = _state.asStateFlow()

    init {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val today = sdf.format(Date())
        _state.update { it.copy(startDate = today, endDate = today) }
    }

    fun loadMembers(clubId: Int) {
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            memberRepository.getMembers(
                MemberRequest(cId = companyId, clubId = clubId.toString(), status = 0),
                forceRefresh = true
            ).collect { result ->
                if (result is ApiResult.Success) {
                    _state.update { it.copy(members = (result.data ?: emptyList()).map { dto -> dto.toUi() }) }
                }
            }
        }
    }

    fun getReports(clubIds: String, memberIds: String, startDate: String, endDate: String) {
        _state.update { 
            it.copy(
                startDate = startDate, 
                endDate = endDate, 
                selectedClubId = clubIds.toIntOrNull() ?: 0,
                selectedMemberIds = if (memberIds.isEmpty()) setOf("0") else memberIds.split(",").toSet()
            ) 
        }
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            val request = ReportRequest(
                cId = companyId,
                clubIds = clubIds,
                startDate = startDate,
                endDate = endDate,
                ids = memberIds
            )
            reportRepository.getReports(request).collect { result ->
                when (result) {
                    ApiResult.Loading -> _state.update { it.copy(isLoading = true) }
                    is ApiResult.Success -> _state.update { it.copy(isLoading = false, reports = result.data.data ?: emptyList()) }
                    is ApiResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun getTransactions(memberId: Int) {
        _state.update { it.copy(selectedTransactionMemberId = memberId) }
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            val request = TransactionRequest(
                cId = companyId,
                memberId = memberId
            )
            reportRepository.getTransactions(request).collect { result ->
                when (result) {
                    ApiResult.Loading -> _state.update { it.copy(isLoading = true) }
                    is ApiResult.Success -> {
                        val transactions = result.data.data ?: emptyList()
                        val totalAmount = transactions.sumOf { it.price ?: 0 }
                        _state.update {
                            it.copy(
                                isLoading = false,
                                transactions = transactions,
                                totalAmount = totalAmount,
                                totalCount = transactions.size
                            )
                        }
                    }
                    is ApiResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
