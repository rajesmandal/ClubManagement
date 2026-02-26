package com.sohitechnology.clubmanagement.data.model

data class UpdateMemberRequest(
    val id: Int,
    val memberId: String,
    val name: String,
    val userName: String,
    val password: String,
    val image: String,
    val status: String,
    val gender: String,
    val contactNo: String,
    val emailId: String,
    val clubName: String,
    val clubId: Int,
    val birthDay: String,
    val hireDay: String,
    val address: String,
    val nationality: String,
    val startDate: String,
    val expiryDate: String
)
