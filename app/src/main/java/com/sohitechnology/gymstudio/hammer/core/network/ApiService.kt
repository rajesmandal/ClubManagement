package com.sohitechnology.gymstudio.hammer.core.network

import com.sohitechnology.gymstudio.hammer.data.model.AddMemberRequest
import com.sohitechnology.gymstudio.hammer.data.model.AddMemberResponse
import com.sohitechnology.gymstudio.hammer.data.model.ClubRequest
import com.sohitechnology.gymstudio.hammer.data.model.ClubResponse
import com.sohitechnology.gymstudio.hammer.data.model.CredentialUpdateRequest
import com.sohitechnology.gymstudio.hammer.data.model.CredentialUpdateResponse
import com.sohitechnology.gymstudio.hammer.data.model.ImageUploadRequest
import com.sohitechnology.gymstudio.hammer.data.model.ImageUploadResponse
import com.sohitechnology.gymstudio.hammer.data.model.LoginRequest
import com.sohitechnology.gymstudio.hammer.data.model.LoginResponse
import com.sohitechnology.gymstudio.hammer.data.model.LogoutRequest
import com.sohitechnology.gymstudio.hammer.data.model.LogoutResponse
import com.sohitechnology.gymstudio.hammer.data.model.MemberCountRequest
import com.sohitechnology.gymstudio.hammer.data.model.MemberCountResponse
import com.sohitechnology.gymstudio.hammer.data.model.MemberExpiryRequest
import com.sohitechnology.gymstudio.hammer.data.model.MemberExpiryResponse
import com.sohitechnology.gymstudio.hammer.data.model.MemberRequest
import com.sohitechnology.gymstudio.hammer.data.model.MemberResponse
import com.sohitechnology.gymstudio.hammer.data.model.MemberRenewRequest
import com.sohitechnology.gymstudio.hammer.data.model.MemberRenewResponse
import com.sohitechnology.gymstudio.hammer.data.model.PackageRequest
import com.sohitechnology.gymstudio.hammer.data.model.PackageResponse
import com.sohitechnology.gymstudio.hammer.data.model.ProfileRequest
import com.sohitechnology.gymstudio.hammer.data.model.ProfileResponse
import com.sohitechnology.gymstudio.hammer.data.model.ProfileUpdateRequest
import com.sohitechnology.gymstudio.hammer.data.model.ProfileUpdateResponse
import com.sohitechnology.gymstudio.hammer.data.model.ReportRequest
import com.sohitechnology.gymstudio.hammer.data.model.ReportResponse
import com.sohitechnology.gymstudio.hammer.data.model.TransactionRequest
import com.sohitechnology.gymstudio.hammer.data.model.TransactionResponse
import com.sohitechnology.gymstudio.hammer.data.model.UpdateFcmTokenRequest
import com.sohitechnology.gymstudio.hammer.data.model.UpdateFcmTokenResponse
import com.sohitechnology.gymstudio.hammer.data.model.UpdateMemberRequest
import com.sohitechnology.gymstudio.hammer.data.model.UpdateMemberResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

    @POST("api/admin/or/staff/login") // login endpoint
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/api/admin/or/staff/get/member") // member list endpoint
    suspend fun getMembers(@Body request: MemberRequest): MemberResponse

    @POST("/api/admin/or/staff/get/club") // club list endpoint
    suspend fun getClubs(
        @Body request: ClubRequest
    ): ClubResponse

    @POST("/api/admin/or/staff/member/update") //edit or update member endpoint
    suspend fun updateMember(@Body updateMemberRequest: UpdateMemberRequest): UpdateMemberResponse

    @POST("/api/admin/or/staff/member/add") //add new member endpoint
    suspend fun addMember(@Body addMemberRequest: AddMemberRequest): AddMemberResponse

    @POST("/api/admin/or/staff/package/get") //package list endpoint
    suspend fun getPackages(@Body packageRequest: PackageRequest): PackageResponse

    @POST("/api/admin/or/staff/member/renew") //renew member endpoint
    suspend fun renewMember(@Body memberRenewRequest: MemberRenewRequest): MemberRenewResponse

    @POST("/api/admin/or/staff/report/check-in-out") //check in out or Report of members and staff end point
    suspend fun report(@Body reportRequest: ReportRequest): ReportResponse

    @POST("/api/admin/or/staff/member/transaction/get") //transaction list endpoint
    suspend fun transaction(@Body transactionRequest: TransactionRequest): TransactionResponse

    @POST("/api/authentication/logout")
    suspend fun logout(@Body logoutRequest: LogoutRequest): LogoutResponse

    @POST("/api/admin/or/staff/get/member/count")
    suspend fun memberCount(@Body memberCountRequest: MemberCountRequest): MemberCountResponse

    @POST("/api/admin/or/staff/get/member/expiry")
    suspend fun memberExpiry(@Body memberExpiryRequest: MemberExpiryRequest): MemberExpiryResponse

    @POST("/api/admin/or/staff/profile/get")
    suspend fun getProfile(@Body profileRequest: ProfileRequest): ProfileResponse

    @POST("/api/admin/or/staff/profile/update")
    suspend fun updateProfile(@Body profileUpdateRequest: ProfileUpdateRequest): ProfileUpdateResponse

    @POST("/api/admin/or/staff/credential/update")
    suspend fun updateCredential(@Body credentialUpdateRequest: CredentialUpdateRequest): CredentialUpdateResponse

    @POST("/api/admin/or/staff/update/fcm-token")
    suspend fun updateFcmToken(@Body updateFcmTokenRequest: UpdateFcmTokenRequest): UpdateFcmTokenResponse

    @POST
    suspend fun uploadImage(@Url url: String, @Body request: ImageUploadRequest): ImageUploadResponse
}
