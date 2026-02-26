package com.sohitechnology.clubmanagement.data.model

data class MemberExpiryResponse(
    val data: List<MemberExpiryData>? = null,
    val error: Boolean? = null,
    val message: String? = null,
    val success: Boolean? = null
)
