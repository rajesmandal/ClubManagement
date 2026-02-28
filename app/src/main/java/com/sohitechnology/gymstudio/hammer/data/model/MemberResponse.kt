package com.sohitechnology.gymstudio.hammer.data.model

data class MemberResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val error: Boolean? = null,
    val data: List<MemberDto>? = null
)
