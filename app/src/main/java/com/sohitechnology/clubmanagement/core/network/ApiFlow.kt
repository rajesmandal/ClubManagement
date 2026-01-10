package com.sohitechnology.clubmanagement.core.network

import com.google.gson.Gson
import com.sohitechnology.clubmanagement.core.common.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun <T> apiFlow(
    gson: Gson,
    apiCall: suspend () -> T
): Flow<ApiResult<T>> = flow {

    emit(ApiResult.Loading) // loading start

    val result = safeApiCall(gson) {
        apiCall()
    }

    emit(result) // success or error

}.flowOn(Dispatchers.IO) // network thread
