package com.sohitechnology.gymstudio.hammer.data.model

data class LoginRequest(
    val userName: String,   // username
    val password: String,   // password
    val cId: Int,           // club id
    val deviceId: String    // device id
)
