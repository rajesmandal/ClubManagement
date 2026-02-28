package com.sohitechnology.gymstudio.hammer.data.model

data class ProfileUpdateRequest(
    val address: String,
    val cId: Int,
    val companyName: String,
    val contactNo: String,
    val country: String,
    val emailId: String,
    val personalName: String
)