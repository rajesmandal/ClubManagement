package com.sohitechnology.clubmanagement.data.model

data class ClubResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val error: Boolean? = null,
    val data: List<ClubDto>? = null
)
