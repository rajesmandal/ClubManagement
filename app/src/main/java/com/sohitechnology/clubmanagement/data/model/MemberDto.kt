package com.sohitechnology.clubmanagement.data.model

data class MemberDto(
    val id: Int,
    val memberId: String,
    val name: String,
    val userName: String,
    val image: String,
    val status: String,
    val gender: String,
    val contactNo: String,
    val emailId: String,
    val clubName: String,
    val birthDay: String,
    val hireDay: String,
    val address: String,
    val nationality: String,
    val startDate: String,
    val expiryDate: String
)

