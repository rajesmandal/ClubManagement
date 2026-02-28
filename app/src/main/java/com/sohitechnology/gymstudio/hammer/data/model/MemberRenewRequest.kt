package com.sohitechnology.gymstudio.hammer.data.model

data class MemberRenewRequest(
    val cId: Int,
    val memberData: String,
    val planData: String
)