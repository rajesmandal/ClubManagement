package com.sohitechnology.clubmanagement.data.model

data class TransactionRequest(
    val cId: Int,
    val id: Int,
    val startDate: String,
    val endDate: String
)