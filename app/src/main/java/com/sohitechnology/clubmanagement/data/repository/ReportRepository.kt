package com.sohitechnology.clubmanagement.data.repository

import com.google.gson.Gson
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.network.ApiService
import com.sohitechnology.clubmanagement.core.network.safeApiCall
import com.sohitechnology.clubmanagement.data.model.ReportRequest
import com.sohitechnology.clubmanagement.data.model.ReportResponse
import com.sohitechnology.clubmanagement.data.model.TransactionRequest
import com.sohitechnology.clubmanagement.data.model.TransactionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val api: ApiService,
    private val gson: Gson
) {
    fun getReports(request: ReportRequest): Flow<ApiResult<ReportResponse>> = flow {
        emit(ApiResult.Loading)
        emit(safeApiCall(gson) {
            api.report(request)
        })
    }.flowOn(Dispatchers.IO)

    fun getTransactions(request: TransactionRequest): Flow<ApiResult<TransactionResponse>> = flow {
        emit(ApiResult.Loading)
        emit(safeApiCall(gson) {
            api.transaction(request)
        })
    }.flowOn(Dispatchers.IO)
}
