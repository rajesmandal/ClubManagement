package com.sohitechnology.gymstudio.hammer.data.model

data class MemberReportRequest(
    val cId: Int,
    val startDate: String,
    val endDate: String,
    val id: String
)
