package com.sohitechnology.clubmanagement.data.model

data class ProfileUpdateRequest(
    val address: String,
    val cId: Int,
    val companyName: String,
    val contactNo: String,
    val country: String,
    val emailId: String,
    val personalName: String
)