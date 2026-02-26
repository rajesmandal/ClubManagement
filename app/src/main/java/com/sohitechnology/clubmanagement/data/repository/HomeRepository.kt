package com.sohitechnology.clubmanagement.data.repository

import com.google.gson.Gson
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.network.ApiService
import com.sohitechnology.clubmanagement.core.network.safeApiCall
import com.sohitechnology.clubmanagement.data.model.MemberCountRequest
import com.sohitechnology.clubmanagement.data.model.MemberCountResponse
import com.sohitechnology.clubmanagement.data.model.MemberExpiryRequest
import com.sohitechnology.clubmanagement.data.model.MemberExpiryResponse
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
}
