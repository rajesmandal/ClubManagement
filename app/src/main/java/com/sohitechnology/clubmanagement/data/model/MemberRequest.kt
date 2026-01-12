package com.sohitechnology.clubmanagement.data.model

data class MemberRequest(
    val cId: Int,       // company id
    val clubId: String, // club id
    val status: Int     // filter
)
