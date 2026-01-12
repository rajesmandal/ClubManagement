package com.sohitechnology.clubmanagement.data.model

data class ClubResponse(
    val success: Boolean,
    val message: String,
    val error: Boolean,
    val data: List<ClubDto>
)
