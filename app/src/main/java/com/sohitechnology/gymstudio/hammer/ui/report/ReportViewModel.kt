package com.sohitechnology.gymstudio.hammer.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.gymstudio.hammer.core.common.ApiResult
import com.sohitechnology.gymstudio.hammer.core.session.AppDataStore
import com.sohitechnology.gymstudio.hammer.core.session.SessionKeys
import com.sohitechnology.gymstudio.hammer.data.cache.ReportCache
import com.sohitechnology.gymstudio.hammer.data.model.ReportData
import com.sohitechnology.gymstudio.hammer.data.model.MemberRequest
import com.sohitechnology.gymstudio.hammer.data.model.ReportRequest
import com.sohitechnology.gymstudio.hammer.data.model.TransactionData
import com.sohitechnology.gymstudio.hammer.data.model.TransactionRequest
import com.sohitechnology.gymstudio.hammer.data.repository.MemberRepository
import com.sohitechnology.gymstudio.hammer.data.repository.ReportRepository
import com.sohitechnology.gymstudio.hammer.ui.member.toUi
import com.sohitechnology.gymstudio.hammer.ui.member.MemberUiModel
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
    val transactionStartDate: String = "",
    val transactionEndDate: String = "",
    val selectedClubId: Int = 0,
    val selectedMemberId: String = "0",
    val selectedTransactionMemberId: Int = 0,
    val selectedTransactionClubId: Int = 0
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
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = sdf.format(Date())
        _state.update { it.copy(startDate = today, endDate = today, transactionStartDate = today, transactionEndDate = today) }
        
        // Initialize from cache
        _state.update { 
            it.copy(
                reports = ReportCache.reports ?: emptyList(),
                transactions = ReportCache.transactions ?: emptyList(),
                totalAmount = ReportCache.totalAmount,
                totalCount = ReportCache.totalCount
            )
        }
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

    private fun formatDateForApi(dateStr: String): String {
        return try {
            val inputSdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val outputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputSdf.parse(dateStr)
            if (date != null) outputSdf.format(date) else dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getReports(clubIds: String, memberIds: String, startDate: String, endDate: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && ReportCache.reports != null) {
            return
        }

        _state.update { 
            it.copy(
                startDate = startDate, 
                endDate = endDate, 
                selectedClubId = clubIds.toIntOrNull() ?: 0,
                selectedMemberId = memberIds
            ) 
        }
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            val request = ReportRequest(
                cId = companyId,
                clubIds = clubIds,
                startDate = formatDateForApi(startDate),
                endDate = formatDateForApi(endDate),
                ids = memberIds
            )
            reportRepository.getReports(request).collect { result ->
                when (result) {
                    is ApiResult.Loading -> _state.update { it.copy(isLoading = true) }
                    is ApiResult.Success -> {
                        val reports = result.data.data ?: emptyList()
                        _state.update { it.copy(isLoading = false, reports = reports) }
                        ReportCache.reports = reports
                    }
                    is ApiResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun getTransactions(clubId: Int, memberId: Int, startDate: String, endDate: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && ReportCache.transactions != null) {
            return
        }

        // 0 is "All", so it is now a valid selection to trigger API call
        _state.update { it.copy(selectedTransactionClubId = clubId, selectedTransactionMemberId = memberId, transactionStartDate = startDate, transactionEndDate = endDate) }
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            
            val request = TransactionRequest(
                cId = companyId,
                id = memberId,
                startDate = formatDateForApi(startDate),
                endDate = formatDateForApi(endDate),
                clubId = clubId
            )
            reportRepository.getTransactions(request).collect { result ->
                when (result) {
                    is ApiResult.Loading -> _state.update { it.copy(isLoading = true) }
                    is ApiResult.Success -> {
                        val transactions = result.data.data ?: emptyList()
                        val totalAmount = transactions.sumOf { it.price ?: 0 }
                        val totalCount = transactions.size
                        _state.update {
                            it.copy(
                                isLoading = false,
                                transactions = transactions,
                                totalAmount = totalAmount,
                                totalCount = totalCount
                            )
                        }
                        ReportCache.transactions = transactions
                        ReportCache.totalAmount = totalAmount
                        ReportCache.totalCount = totalCount
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
