package com.sohitechnology.clubmanagement.data.model

import android.content.Context
import com.google.gson.Gson
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.network.ApiService
import com.sohitechnology.clubmanagement.core.network.apiFlow
import com.sohitechnology.clubmanagement.core.session.AppDataStore
import com.sohitechnology.clubmanagement.core.util.DeviceUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
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
            api.login(request.copy(deviceId = deviceId))
        }
    }

    fun logout(request: LogoutRequest): Flow<ApiResult<LogoutResponse>> {
        return apiFlow(gson) {
            api.logout(request)
        }
    }

    fun updateCredential(request: CredentialUpdateRequest): Flow<ApiResult<CredentialUpdateResponse>> {
        return apiFlow(gson) {
            api.updateCredential(request)
        }
    }

    fun getProfile(request: ProfileRequest): Flow<ApiResult<ProfileResponse>> {
        return apiFlow(gson) {
            api.getProfile(request)
        }
    }

    fun updateProfile(request: ProfileUpdateRequest): Flow<ApiResult<ProfileUpdateResponse>> {
        return apiFlow(gson) {
            api.updateProfile(request)
        }
    }
}
