package com.sohitechnology.clubmanagement.core.network

import com.sohitechnology.clubmanagement.data.model.LoginRequest
import com.sohitechnology.clubmanagement.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/admin/or/staff/login") // login endpoint
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
