package com.sohitechnology.gymstudio.hammer.data.model

data class ReportRequest(
    val cId: Int,
    val clubIds: String,
    val endDate: String,
    val ids: String,
    val startDate: String
)