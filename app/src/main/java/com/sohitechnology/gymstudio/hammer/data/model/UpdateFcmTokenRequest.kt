package com.sohitechnology.gymstudio.hammer.data.model

data class UpdateFcmTokenRequest(
    val cId: Int,
    val fcmToken: String,
    val userId: Int
)