package com.sohitechnology.gymstudio.hammer.ui.member.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.gymstudio.hammer.core.common.ApiResult
import com.sohitechnology.gymstudio.hammer.core.session.AppDataStore
import com.sohitechnology.gymstudio.hammer.core.session.SessionKeys
import com.sohitechnology.gymstudio.hammer.data.cache.ReportCache
import com.sohitechnology.gymstudio.hammer.data.model.MemberReportRequest
import com.sohitechnology.gymstudio.hammer.data.model.ReportData
import com.sohitechnology.gymstudio.hammer.data.model.TransactionRequest
import com.sohitechnology.gymstudio.hammer.data.repository.ReportRepository
import com.sohitechnology.gymstudio.hammer.ui.report.ReportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class GymUsageStats(
    val totalMinutes: Long = 0,
    val averageMinutesPerDay: Long = 0,
    val dailyMinutes: Map<String, Long> = emptyMap(),
    val gymVisitCount: Int = 0,
    val weekTotalMinutes: Long = 0,
    val weekAverageMinutesPerDay: Long = 0,
    val weekVisitCount: Int = 0,
    val currentStreak: Int = 0,
    val bestDay: String = "N/A",
    val consistency: Int = 0,
    val activeDates: Set<String> = emptySet()
)

@HiltViewModel
class MemberReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(ReportState(isAdmin = false))
    val state = _state.asStateFlow()

    val usageStats = _state.map { state ->
        calculateUsageStats(state.reports)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GymUsageStats())

    init {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val today = sdf.format(calendar.time)
        
        // Calculate last month start date
        calendar.add(Calendar.MONTH, -1)
        val lastMonthStart = sdf.format(calendar.time)
        
        _state.update { it.copy(startDate = lastMonthStart, endDate = today, transactionStartDate = lastMonthStart, transactionEndDate = today) }
        
        getReports(lastMonthStart, today)
        getTransactions(lastMonthStart, today)
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

    fun getReports(startDate: String, endDate: String, forceRefresh: Boolean = false) {
        _state.update { it.copy(startDate = startDate, endDate = endDate) }
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            val userId = dataStore.readOnce(SessionKeys.USER_ID, 0)

            val request = MemberReportRequest(
                cId = companyId,
                startDate = formatDateForApi(startDate),
                endDate = formatDateForApi(endDate),
                id = userId.toString()
            )
            reportRepository.getMemberReports(request).collect { result ->
                when (result) {
                    is ApiResult.Loading -> _state.update { it.copy(isLoading = true) }
                    is ApiResult.Success -> {
                        val reports = result.data.data ?: emptyList()
                        _state.update { it.copy(isLoading = false, reports = reports) }
                    }
                    is ApiResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun getTransactions(startDate: String, endDate: String, forceRefresh: Boolean = false) {
        _state.update { it.copy(transactionStartDate = startDate, transactionEndDate = endDate) }
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            val userId = dataStore.readOnce(SessionKeys.USER_ID, 0)
            val clubId = dataStore.readOnce(SessionKeys.CLUB_ID, 0)

            val request = TransactionRequest(
                cId = companyId,
                id = userId,
                startDate = formatDateForApi(startDate),
                endDate = formatDateForApi(endDate),
                clubId = clubId
            )

            reportRepository.getMemberTransactions(request).collect { result ->
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
                    }
                    is ApiResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    private fun calculateUsageStats(reports: List<ReportData>): GymUsageStats {
        if (reports.isEmpty()) return GymUsageStats()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayDateSdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        
        val now = Calendar.getInstance()
        val oneWeekAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }

        // Group reports by day
        val reportsByDay = reports.filter { it.checkTime != null && it.punchName != null }
            .groupBy { 
                try {
                    val date = sdf.parse(it.checkTime!!)
                    if (date != null) dateSdf.format(date) else ""
                } catch (e: Exception) {
                    ""
                }
            }.filter { it.key.isNotEmpty() }

        val dailyMinutes = mutableMapOf<String, Long>()
        var totalMinutes = 0L
        var visitCount = 0
        
        var weekTotalMinutes = 0L
        var weekVisitCount = 0
        val weekDailyMinutesCount = mutableSetOf<String>()

        var maxPunches = 0
        var bestDayStr = "N/A"

        reportsByDay.forEach { (dateStr, dayReports) ->
            val date = try { dateSdf.parse(dateStr) } catch (e: Exception) { null }
            val isWithinLastWeek = date != null && date.after(oneWeekAgo.time)

            if (dayReports.size > maxPunches) {
                maxPunches = dayReports.size
                bestDayStr = date?.let { displayDateSdf.format(it) } ?: "N/A"
            }

            val sortedReports = dayReports.sortedBy { it.checkTime }
            var dayTotal = 0L
            var lastInTime: Long? = null

            sortedReports.forEach { report ->
                val time = try { sdf.parse(report.checkTime!!).time } catch (e: Exception) { null }
                if (time != null) {
                    val punch = report.punchName!!.lowercase()
                    if (punch.contains("in")) {
                        lastInTime = time
                        visitCount++
                        if (isWithinLastWeek) weekVisitCount++
                    } else if (punch.contains("out") && lastInTime != null) {
                        val duration = time - lastInTime!!
                        val mins = duration / (1000 * 60)
                        dayTotal += mins
                        totalMinutes += mins
                        if (isWithinLastWeek) weekTotalMinutes += mins
                        lastInTime = null
                    }
                }
            }
            if (dayTotal > 0) {
                dailyMinutes[dateStr] = dayTotal
                if (isWithinLastWeek) weekDailyMinutesCount.add(dateStr)
            }
        }

        // Calculate Streak
        val sortedDates = reportsByDay.keys.sortedDescending()
        var streak = 0
        if (sortedDates.isNotEmpty()) {
            val cal = Calendar.getInstance()
            // Check if user punched today or yesterday to continue/start streak
            val todayStr = dateSdf.format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = dateSdf.format(cal.time)

            if (sortedDates[0] == todayStr || sortedDates[0] == yesterdayStr) {
                streak = 1
                var currentCal = Calendar.getInstance()
                if (sortedDates[0] == yesterdayStr) currentCal.add(Calendar.DAY_OF_YEAR, -1)
                
                while (true) {
                    currentCal.add(Calendar.DAY_OF_YEAR, -1)
                    val prevDateStr = dateSdf.format(currentCal.time)
                    if (reportsByDay.containsKey(prevDateStr)) {
                        streak++
                    } else {
                        break
                    }
                }
            }
        }

        val avg = if (dailyMinutes.isNotEmpty()) totalMinutes / dailyMinutes.size else 0
        val weekAvg = if (weekDailyMinutesCount.isNotEmpty()) weekTotalMinutes / weekDailyMinutesCount.size else 0
        
        // Consistency: Active Days / Total Days in period (roughly 30)
        val consistency = (dailyMinutes.size.toFloat() / 30f * 100f).toInt().coerceAtMost(100)

        return GymUsageStats(
            totalMinutes = totalMinutes,
            averageMinutesPerDay = avg,
            dailyMinutes = dailyMinutes,
            gymVisitCount = visitCount,
            weekTotalMinutes = weekTotalMinutes,
            weekAverageMinutesPerDay = weekAvg,
            weekVisitCount = weekVisitCount,
            currentStreak = streak,
            bestDay = bestDayStr,
            consistency = consistency,
            activeDates = reportsByDay.keys
        )
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
