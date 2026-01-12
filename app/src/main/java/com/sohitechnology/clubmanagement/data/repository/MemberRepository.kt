package com.sohitechnology.clubmanagement.data.repository

import com.google.gson.Gson
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.network.ApiService
import com.sohitechnology.clubmanagement.core.network.safeApiCall
import com.sohitechnology.clubmanagement.data.cache.MemberCache
import com.sohitechnology.clubmanagement.data.model.MemberDto
import com.sohitechnology.clubmanagement.data.model.MemberRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MemberRepository @Inject constructor(
    private val api: ApiService,
    private val gson: Gson
) {

    fun getMembers(
        request: MemberRequest,
        forceRefresh: Boolean
    ): Flow<ApiResult<List<MemberDto>>> = flow {

        if (!forceRefresh && MemberCache.members != null) {
            emit(ApiResult.Success(MemberCache.members!!)) // cache
            return@flow
        }

        emit(ApiResult.Loading) // loading

        when (val result = safeApiCall(gson) {
            api.getMembers(request)
        }) {
            is ApiResult.Success -> {
                val list = result.data.data
                MemberCache.members = list // cache save
                emit(ApiResult.Success(list))
            }

            is ApiResult.Error -> emit(result)

            else -> Unit
        }

    }.flowOn(Dispatchers.IO)
}
