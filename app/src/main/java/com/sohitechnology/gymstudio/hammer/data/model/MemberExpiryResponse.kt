package com.sohitechnology.gymstudio.hammer.data.model

data class MemberExpiryResponse(
    val data: List<MemberExpiryData>? = null,
    val error: Boolean? = null,
    val message: String? = null,
    val success: Boolean? = null
)
