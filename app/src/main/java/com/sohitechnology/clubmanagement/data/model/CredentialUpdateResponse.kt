package com.sohitechnology.clubmanagement.data.model

data class CredentialUpdateResponse(
    val `data`: List<Any>,
    val error: Boolean,
    val message: String,
    val success: Boolean
)