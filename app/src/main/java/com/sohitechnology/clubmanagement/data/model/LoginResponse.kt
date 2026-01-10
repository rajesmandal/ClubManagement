package com.sohitechnology.clubmanagement.data.model

data class LoginResponse(
    val success: Boolean,   // api success
    val message: String,    // api message
    val error: Boolean,     // error flag
    val data: LoginData?    // user data
)

data class LoginData(
    val userId: Int,            // user id
    val role: String,           // Admin / Staff
    val userName: String,       // username
    val fullName: String,       // full name
    val accessToken: String,    // auth token
    val refreshToken: String,   // refresh token
    val expiresIn: String,      // expiry
    val cId: Int,               // club id
    val profileImage: String    // profile image
)
