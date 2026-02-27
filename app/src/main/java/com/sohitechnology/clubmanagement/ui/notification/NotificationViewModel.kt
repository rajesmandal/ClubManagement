package com.sohitechnology.clubmanagement.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import com.sohitechnology.clubmanagement.data.model.MemberExpiryRequest
import com.sohitechnology.clubmanagement.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val dataStore: AppDataStore
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun getNotifications() {
        viewModelScope.launch {
            val companyIdStr = dataStore.readOnce(SessionKeys.COMPANY_ID, "0")
            val companyId = companyIdStr.toIntOrNull() ?: 0
            
            homeRepository.getMemberExpiry(MemberExpiryRequest(cId = companyId)).collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        _isLoading.value = true
                        _error.value = null
                    }
                    is ApiResult.Success -> {
                        _isLoading.value = false
                        val members = result.data.data ?: emptyList()
                        _notifications.value = processMembersToNotifications(members)
                    }
                    is ApiResult.Error -> {
                        _isLoading.value = false
                        _error.value = result.message
                    }
                }
            }
        }
    }

    private fun processMembersToNotifications(members: List<com.sohitechnology.clubmanagement.data.model.MemberExpiryData>): List<NotificationItem> {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val notificationList = mutableListOf<NotificationItem>()

        members.forEach { member ->
            val expiryDateStr = member.expiryDate ?: return@forEach
            try {
                val expiryDate = sdf.parse(expiryDateStr) ?: return@forEach
                val diffInMillis = expiryDate.time - today.time
                val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                
                val name = member.name ?: "Member"
                val id = member.id ?: 0

                when {
                    diffInDays == 7L -> {
                        notificationList.add(
                            NotificationItem(
                                id = id,
                                title = "Membership Expiring Soon",
                                message = "$name's membership will expire in 7 days ($expiryDateStr).",
                                time = "Upcoming",
                                isRead = false,
                                date = expiryDate
                            )
                        )
                    }
                    diffInDays == 0L -> {
                        notificationList.add(
                            NotificationItem(
                                id = id,
                                title = "Membership Expired Today",
                                message = "$name's membership has expired today.",
                                time = "Today",
                                isRead = false,
                                date = expiryDate
                            )
                        )
                    }
                    diffInDays in -7L..-1L -> {
                        val daysPast = -diffInDays
                        notificationList.add(
                            NotificationItem(
                                id = id,
                                title = "Membership Expired",
                                message = "$name's membership expired $daysPast days ago ($expiryDateStr).",
                                time = "$daysPast days ago",
                                isRead = true,
                                date = expiryDate
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Skip invalid dates
            }
        }
        
        // Sort by unread first, then by date (most recent first)
        return notificationList.sortedWith(
            compareBy<NotificationItem> { it.isRead }
                .thenByDescending { it.date }
        )
    }

    fun markAsRead(id: Int) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }
}
