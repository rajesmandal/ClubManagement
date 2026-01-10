package com.sohitechnology.clubmanagement.data.model

data class LoginRequest(
    val userName: String,   // username
    val password: String,   // password
    val cId: Int,           // club id
    val deviceId: String    // device id
)
