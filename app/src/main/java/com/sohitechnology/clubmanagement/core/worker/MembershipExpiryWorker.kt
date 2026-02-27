package com.sohitechnology.clubmanagement.core.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.sohitechnology.clubmanagement.core.network.ApiService
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.session.SessionKeys
import com.sohitechnology.clubmanagement.core.util.NotificationHelper
import com.sohitechnology.clubmanagement.data.model.MemberExpiryRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@HiltWorker
class MembershipExpiryWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: ApiService,
    private val dataStore: AppDataStore,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    private val TAG = "MembershipExpiryWorker"

    override suspend fun doWork(): Result {
        Log.d(TAG, "Worker started...")
        
        val companyId = dataStore.readOnce(SessionKeys.COMPANY_ID, "0").toIntOrNull() ?: 0
        Log.d(TAG, "Company ID: $companyId")
        
        if (companyId == 0) {
            Log.e(TAG, "Company ID is 0, skipping check.")
            return Result.success()
        }

        return try {
            val response = api.memberExpiry(MemberExpiryRequest(cId = companyId))
            Log.d(TAG, "API Response success: ${response.success}")
            
            if (response.success == true && response.data != null) {
                Log.d(TAG, "Fetched ${response.data.size} members for expiry check.")
                processMembers(response.data)
            } else {
                Log.w(TAG, "API returned success=false or null data.")
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in MembershipExpiryWorker: ${e.message}", e)
            Result.retry()
        }
    }

    private fun processMembers(members: List<com.sohitechnology.clubmanagement.data.model.MemberExpiryData>) {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        members.forEach { member ->
            val expiryDateStr = member.expiryDate ?: return@forEach
            try {
                val expiryDate = sdf.parse(expiryDateStr) ?: return@forEach
                val diffInMillis = expiryDate.time - today.time
                val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

                Log.d(TAG, "Member: ${member.name}, Expiry: $expiryDateStr, DiffDays: $diffInDays")

                val notificationId = member.id ?: return@forEach
                val name = member.name ?: "Member"

                when {
                    diffInDays == 7L -> {
                        notificationHelper.showNotification(
                            id = notificationId,
                            title = "Membership Expiring Soon",
                            message = "$name's membership will expire in 7 days ($expiryDateStr)."
                        )
                    }
                    diffInDays == 0L -> {
                        notificationHelper.showNotification(
                            id = notificationId,
                            title = "Membership Expired Today",
                            message = "$name's membership has expired today."
                        )
                    }
                    diffInDays in -7L..-1L -> {
                        val daysPast = -diffInDays
                        notificationHelper.showNotification(
                            id = notificationId,
                            title = "Membership Expired",
                            message = "$name's membership expired $daysPast days ago ($expiryDateStr)."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing date for ${member.name}: $expiryDateStr")
            }
        }
    }
}
