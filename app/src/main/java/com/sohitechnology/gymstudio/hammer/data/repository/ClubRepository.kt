package com.sohitechnology.gymstudio.hammer.data.repository

import com.google.gson.Gson
import com.sohitechnology.gymstudio.hammer.core.common.ApiResult
import com.sohitechnology.gymstudio.hammer.core.network.ApiService
import com.sohitechnology.gymstudio.hammer.core.network.safeApiCall
import com.sohitechnology.gymstudio.hammer.data.cache.ClubCache
import com.sohitechnology.gymstudio.hammer.data.model.ClubRequest
import com.sohitechnology.gymstudio.hammer.ui.common.DropdownItem
import com.sohitechnology.gymstudio.hammer.ui.common.toDropdownItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ClubRepository @Inject constructor(
    private val api: ApiService,
    private val gson: Gson
) {

    fun getClubs(cId: Int): Flow<ApiResult<List<DropdownItem>>> = flow {

        ClubCache.clubs?.let {
            emit(ApiResult.Success(it)) // cache hit
            return@flow
        }

        emit(ApiResult.Loading) // first time

        when (val result = safeApiCall(gson) {
            api.getClubs(ClubRequest(cId))
        }) {
            is ApiResult.Success -> {
                val dataList = result.data.data ?: emptyList()
                val list =
                    listOf(DropdownItem(0, "All")) +
                            dataList.map { it.toDropdownItem() }

                ClubCache.clubs = list // save cache
                emit(ApiResult.Success(list))
            }

            is ApiResult.Error -> emit(result)

            else -> Unit
        }

    }.flowOn(Dispatchers.IO)
}
