package com.sohitechnology.gymstudio.hammer.data.repository

import com.google.gson.Gson
import com.sohitechnology.gymstudio.hammer.core.common.ApiResult
import com.sohitechnology.gymstudio.hammer.core.network.ApiService
import com.sohitechnology.gymstudio.hammer.core.network.safeApiCall
import com.sohitechnology.gymstudio.hammer.data.model.MemberCountRequest
import com.sohitechnology.gymstudio.hammer.data.model.MemberCountResponse
import com.sohitechnology.gymstudio.hammer.data.model.MemberExpiryRequest
import com.sohitechnology.gymstudio.hammer.data.model.MemberExpiryResponse
import com.sohitechnology.gymstudio.hammer.data.model.UpdateFcmTokenRequest
import com.sohitechnology.gymstudio.hammer.data.model.UpdateFcmTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val api: ApiService,
    private val gson: Gson
) {
    fun getMemberCount(request: MemberCountRequest): Flow<ApiResult<MemberCountResponse>> = flow {
        emit(ApiResult.Loading)
        emit(safeApiCall(gson) {
            api.memberCount(request)
        })
    }.flowOn(Dispatchers.IO)

    fun getMemberExpiry(request: MemberExpiryRequest): Flow<ApiResult<MemberExpiryResponse>> = flow {
        emit(ApiResult.Loading)
        emit(safeApiCall(gson) {
            api.memberExpiry(request)
        })
    }.flowOn(Dispatchers.IO)

    fun updateFcmToken(request: UpdateFcmTokenRequest): Flow<ApiResult<UpdateFcmTokenResponse>> = flow {
        emit(ApiResult.Loading)
        emit(safeApiCall(gson) {
            api.updateFcmToken(request)
        })
    }.flowOn(Dispatchers.IO)
}
