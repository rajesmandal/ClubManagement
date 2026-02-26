package com.sohitechnology.clubmanagement.data.model

data class ProfileUpdateResponse(
    val `data`: List<Any>,
    val error: Boolean,
    val message: String,
    val success: Boolean
)