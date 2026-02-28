package com.sohitechnology.gymstudio.hammer.data.model

data class TransactionRequest(
    val cId: Int,
    val id: Int,
    val startDate: String,
    val endDate: String
)