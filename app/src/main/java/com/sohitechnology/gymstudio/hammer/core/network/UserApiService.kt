package com.sohitechnology.gymstudio.hammer.core.network

import com.sohitechnology.gymstudio.hammer.data.model.LoginRequest
import com.sohitechnology.gymstudio.hammer.data.model.LoginResponse
import com.sohitechnology.gymstudio.hammer.data.model.MemberDetailRequest
import com.sohitechnology.gymstudio.hammer.data.model.MemberDetailResponse
import com.sohitechnology.gymstudio.hammer.data.model.MemberReportRequest
import com.sohitechnology.gymstudio.hammer.data.model.ReportResponse
import com.sohitechnology.gymstudio.hammer.data.model.TransactionRequest
import com.sohitechnology.gymstudio.hammer.data.model.TransactionResponse
import com.sohitechnology.gymstudio.hammer.data.model.UpdateFcmTokenRequest
import com.sohitechnology.gymstudio.hammer.data.model.UpdateFcmTokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {

    @POST("api/member/login") // member login endpoint
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/member/report/check-in-out") // member report endpoint
    suspend fun getReport(@Body request: MemberReportRequest): ReportResponse

    @POST("api/member/transaction/get") // member transaction endpoint
    suspend fun getTransactions(@Body request: TransactionRequest): TransactionResponse

    @POST("/api/member/update/fcm-token") // member fcm token update endpoint
    suspend fun updateFcmToken(@Body request: UpdateFcmTokenRequest): UpdateFcmTokenResponse

    @POST("/api/member/getbyid") // member detail by id endpoint
    suspend fun getById(@Body request: MemberDetailRequest): MemberDetailResponse

}
