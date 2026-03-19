package com.sohitechnology.gymstudio.hammer.data.repository

import android.content.Context
import com.google.gson.Gson
import com.sohitechnology.gymstudio.hammer.core.common.ApiResult
import com.sohitechnology.gymstudio.hammer.core.network.ApiService
import com.sohitechnology.gymstudio.hammer.core.network.UserApiService
import com.sohitechnology.gymstudio.hammer.core.network.apiFlow
import com.sohitechnology.gymstudio.hammer.core.session.AppDataStore
import com.sohitechnology.gymstudio.hammer.core.util.DeviceUtil
import com.sohitechnology.gymstudio.hammer.data.model.CredentialUpdateRequest
import com.sohitechnology.gymstudio.hammer.data.model.CredentialUpdateResponse
import com.sohitechnology.gymstudio.hammer.data.model.ImageUploadRequest
import com.sohitechnology.gymstudio.hammer.data.model.ImageUploadResponse
import com.sohitechnology.gymstudio.hammer.data.model.LoginRequest
import com.sohitechnology.gymstudio.hammer.data.model.LoginResponse
import com.sohitechnology.gymstudio.hammer.data.model.LogoutRequest
import com.sohitechnology.gymstudio.hammer.data.model.LogoutResponse
import com.sohitechnology.gymstudio.hammer.data.model.ProfileRequest
import com.sohitechnology.gymstudio.hammer.data.model.ProfileResponse
import com.sohitechnology.gymstudio.hammer.data.model.ProfileUpdateRequest
import com.sohitechnology.gymstudio.hammer.data.model.ProfileUpdateResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val userApi: UserApiService,
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

    fun memberLogin(request: LoginRequest): Flow<ApiResult<LoginResponse>> {
        return apiFlow(gson) {
            val deviceId = DeviceUtil.getOrCreateDeviceId(context, dataStore)
            userApi.login(request.copy(deviceId = deviceId))
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

    fun uploadImage(request: ImageUploadRequest): Flow<ApiResult<ImageUploadResponse>> {
        return apiFlow(gson) {
            api.uploadImage("https://img.gymstudio.in/api/image-manager/upload/image", request)
        }
    }
}