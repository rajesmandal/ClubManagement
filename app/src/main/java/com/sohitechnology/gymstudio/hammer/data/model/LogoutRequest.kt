package com.sohitechnology.gymstudio.hammer.data.model

data class LogoutRequest(
    val cId: Int,
    val userId: Int? = null
)