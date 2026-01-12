package com.sohitechnology.clubmanagement.core.network

import com.sohitechnology.clubmanagement.data.model.ClubRequest
import com.sohitechnology.clubmanagement.data.model.ClubResponse
import com.sohitechnology.clubmanagement.data.model.LoginRequest
import com.sohitechnology.clubmanagement.data.model.LoginResponse
import com.sohitechnology.clubmanagement.data.model.MemberRequest
import com.sohitechnology.clubmanagement.data.model.MemberResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/admin/or/staff/login") // login endpoint
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/api/admin/or/staff/get/member") // member list endpoint
    suspend fun getMembers(@Body request: MemberRequest): MemberResponse

    @POST("/api/admin/or/staff/get/club") // club list endpoint
    suspend fun getClubs(
        @Body request: ClubRequest
    ): ClubResponse


}
