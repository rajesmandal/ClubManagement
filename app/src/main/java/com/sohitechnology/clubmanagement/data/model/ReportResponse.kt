package com.sohitechnology.clubmanagement.data.model

data class ReportResponse(
    val data: List<ReportData>? = null,
    val error: Boolean? = null,
    val message: String? = null,
    val success: Boolean? = null
)
