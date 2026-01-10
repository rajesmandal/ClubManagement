package com.sohitechnology.clubmanagement.data.model

import android.content.Context
import com.google.gson.Gson
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.network.ApiService
import com.sohitechnology.clubmanagement.core.network.apiFlow
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.util.DeviceUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val gson: Gson,
    @ApplicationContext private val context: Context,
    private val dataStore: AppDataStore
) {

    fun login(request: LoginRequest): Flow<ApiResult<LoginResponse>> {
        return apiFlow(gson) {
            val deviceId = DeviceUtil.getOrCreateDeviceId(context, dataStore)
            api.login(request.copy(deviceId="string")) // api call //temp
        }
    }
}
