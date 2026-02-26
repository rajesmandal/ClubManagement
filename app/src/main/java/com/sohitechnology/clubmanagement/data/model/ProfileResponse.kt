package com.sohitechnology.clubmanagement.data.model

data class ProfileResponse(
    val `data`: ProfileData,
    val error: Boolean,
    val message: String,
    val success: Boolean
)

data class ProfileData(
    val address: String,
    val companyName: String,
    val contactNo: String,
    val country: String,
    val emailId: String,
    val personalName: String
)