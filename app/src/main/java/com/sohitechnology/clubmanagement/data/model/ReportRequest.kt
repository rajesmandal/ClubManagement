package com.sohitechnology.clubmanagement.data.model

data class ReportRequest(
    val cId: Int,
    val clubIds: String,
    val endDate: String,
    val ids: String,
    val startDate: String
)