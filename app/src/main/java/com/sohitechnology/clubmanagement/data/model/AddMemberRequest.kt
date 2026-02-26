package com.sohitechnology.clubmanagement.data.model

data class AddMemberRequest(
    val clubId: Int,
    val contactNo: String,
    val emailId: String,
    val gender: String,
    val image: String,
    val memberId: String,
    val name: String,
    val password: String,
    val userName: String
)