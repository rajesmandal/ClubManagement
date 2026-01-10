package com.sohitechnology.clubmanagement.core.common

sealed class ApiResult<out T> {

    object Loading : ApiResult<Nothing>()

    data class Success<T>(
        val data: T
    ) : ApiResult<T>()

    data class Error(
        val message: String,
        val code: Int? = null
    ) : ApiResult<Nothing>()
}
