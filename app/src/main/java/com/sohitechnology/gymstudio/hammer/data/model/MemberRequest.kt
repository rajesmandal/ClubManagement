package com.sohitechnology.gymstudio.hammer.data.model

data class MemberRequest(
    val cId: Int,       // company id
    val clubId: String, // club id
    val status: Int     // filter
)
