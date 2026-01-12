package com.sohitechnology.clubmanagement.data.model

data class MemberResponse(
    val success: Boolean,
    val message: String,
    val error: Boolean,
    val data: List<MemberDto>
)
