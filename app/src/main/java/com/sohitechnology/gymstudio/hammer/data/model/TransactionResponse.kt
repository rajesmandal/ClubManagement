package com.sohitechnology.gymstudio.hammer.data.model

data class TransactionResponse(
    val data: List<TransactionData>? = null,
    val error: Boolean? = null,
    val message: String? = null,
    val success: Boolean? = null
)
