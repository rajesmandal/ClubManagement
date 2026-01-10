package com.sohitechnology.clubmanagement.core.network

import com.google.gson.Gson
import com.sohitechnology.clubmanagement.core.common.ApiResult
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun mapApiError(
    throwable: Throwable,
    gson: Gson
): ApiResult.Error {

    return when (throwable) {

        is HttpException -> {
            val errorBody = throwable.response()?.errorBody()?.string()

            val backendMessage = try {
                gson.fromJson(errorBody, ErrorResponse::class.java)?.message
            } catch (e: Exception) {
                null
            }

            val message = when (throwable.code()) {
                400 -> backendMessage ?: "Bad request"
                401 -> "Session expired. Please login again."
                403 -> "You are not allowed to perform this action."
                404 -> "Requested data not found."
                409 -> backendMessage ?: "Conflict occurred."
                500 -> "Server error. Try again later."
                else -> backendMessage ?: "Something went wrong."
            }

            ApiResult.Error(message, throwable.code())
        }

        is IOException -> {
            ApiResult.Error("No internet connection. Please check network.")
        }

        is SocketTimeoutException -> {
            ApiResult.Error("Request timeout. Please try again.")
        }

        else -> {
            ApiResult.Error("Unexpected error occurred.")
        }
    }
}
