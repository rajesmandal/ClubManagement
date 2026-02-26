package com.sohitechnology.clubmanagement.data.model

data class MemberRenewRequest(
    val cId: Int,
    val memberData: String,
    val planData: String
)