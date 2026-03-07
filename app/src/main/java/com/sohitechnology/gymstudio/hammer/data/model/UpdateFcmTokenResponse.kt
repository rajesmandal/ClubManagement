package com.sohitechnology.gymstudio.hammer.data.model

data class UpdateFcmTokenResponse(
    val `data`: List<Any>,
    val error: Boolean,
    val message: String,
    val success: Boolean
)