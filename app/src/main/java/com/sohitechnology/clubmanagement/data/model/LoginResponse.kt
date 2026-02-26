package com.sohitechnology.clubmanagement.data.model

data class LoginResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val error: Boolean? = null,
    val data: LoginData? = null
)

data class LoginData(
    val userId: Int? = null,
    val role: String? = null,
    val userName: String? = null,
    val fullName: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: String? = null,
    val cId: Int? = null,
    val profileImage: String? = null
)
