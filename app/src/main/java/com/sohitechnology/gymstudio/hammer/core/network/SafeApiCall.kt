package com.sohitechnology.gymstudio.hammer.core.network

import com.google.gson.Gson
import com.sohitechnology.gymstudio.hammer.core.common.ApiResult

suspend fun <T> safeApiCall(
    gson: Gson,
    apiCall: suspend () -> T
): ApiResult<T> {
    return try {
        ApiResult.Success(apiCall())
    } catch (e: Throwable) {
        e.printStackTrace()
        mapApiError(e, gson)
    }
}
