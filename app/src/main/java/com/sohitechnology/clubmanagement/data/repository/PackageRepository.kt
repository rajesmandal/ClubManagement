package com.sohitechnology.clubmanagement.data.repository

import com.google.gson.Gson
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.network.ApiService
import com.sohitechnology.clubmanagement.core.network.safeApiCall
import com.sohitechnology.clubmanagement.data.model.MemberRenewRequest
import com.sohitechnology.clubmanagement.data.model.MemberRenewResponse
import com.sohitechnology.clubmanagement.data.model.PackageRequest
import com.sohitechnology.clubmanagement.data.model.PackageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PackageRepository @Inject constructor(
    private val api: ApiService,
    private val gson: Gson
) {
    fun getPackages(request: PackageRequest): Flow<ApiResult<PackageResponse>> = flow {
        emit(ApiResult.Loading)
        emit(safeApiCall(gson) {
            api.getPackages(request)
        })
    }.flowOn(Dispatchers.IO)

    fun renewMember(request: MemberRenewRequest): Flow<ApiResult<MemberRenewResponse>> = flow {
        emit(ApiResult.Loading)
        emit(safeApiCall(gson) {
            api.renewMember(request)
        })
    }.flowOn(Dispatchers.IO)
}
