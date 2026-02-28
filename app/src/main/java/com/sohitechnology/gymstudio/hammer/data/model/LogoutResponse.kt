package com.sohitechnology.gymstudio.hammer.data.model

data class LogoutResponse(
    val data: LogoutData? = null,
    val error: Boolean? = null,
    val message: String? = null,
    val success: Boolean? = null
)

class LogoutData
