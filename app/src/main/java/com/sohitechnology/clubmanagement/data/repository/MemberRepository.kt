package com.sohitechnology.clubmanagement.data.repository

import com.google.gson.Gson
import com.sohitechnology.clubmanagement.core.common.ApiResult
import com.sohitechnology.clubmanagement.core.network.ApiService
import com.sohitechnology.clubmanagement.core.network.safeApiCall
import com.sohitechnology.clubmanagement.data.cache.MemberCache
import com.sohitechnology.clubmanagement.data.model.AddMemberRequest
import com.sohitechnology.clubmanagement.data.model.AddMemberResponse
import com.sohitechnology.clubmanagement.data.model.MemberDto
import com.sohitechnology.clubmanagement.data.model.MemberRequest
import com.sohitechnology.clubmanagement.data.model.UpdateMemberRequest
import com.sohitechnology.clubmanagement.data.model.UpdateMemberResponse
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
            emit(ApiResult.Success(MemberCache.members!!))
            return@flow
        }

        emit(ApiResult.Loading)

        when (val result = safeApiCall(gson) {
            api.getMembers(request)
        }) {
            is ApiResult.Success -> {
                val list = result.data.data ?: emptyList()
                MemberCache.members = list
                emit(ApiResult.Success(list))
            }

            is ApiResult.Error -> emit(result)

            else -> Unit
        }

    }.flowOn(Dispatchers.IO)

    fun updateMember(request: UpdateMemberRequest): Flow<ApiResult<UpdateMemberResponse>> = flow {
        emit(ApiResult.Loading)
        emit(safeApiCall(gson) {
            api.updateMember(request)
        })
    }.flowOn(Dispatchers.IO)

    fun addMember(request: AddMemberRequest): Flow<ApiResult<AddMemberResponse>> = flow {
        emit(ApiResult.Loading)
        emit(safeApiCall(gson) {
            api.addMember(request)
        })
    }.flowOn(Dispatchers.IO)
}
