package com.sohitechnology.clubmanagement.ui.member

import com.sohitechnology.clubmanagement.data.model.MemberDto

data class MemberUiModel(
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

fun MemberDto.toUi() = MemberUiModel(
    id = id ?: 0,
    memberId = memberId ?: "",
    name = name ?: "Unknown Member",
    userName = userName ?: "",
    password = password ?: "",
    image = image ?: "",
    status = status ?: "Unknown",
    gender = gender ?: "",
    contactNo = contactNo ?: "",
    emailId = emailId ?: "",
    clubName = clubName ?: "",
    clubId = clubId ?: 0,
    birthDay = birthDay ?: "",
    hireDay = hireDay ?: "",
    address = address ?: "",
    nationality = nationality ?: "",
    startDate = startDate ?: "",
    expiryDate = expiryDate ?: ""
)
