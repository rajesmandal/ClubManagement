package com.sohitechnology.clubmanagement.ui.member

import com.sohitechnology.clubmanagement.data.model.MemberDto

data class MemberUiModel(
    val memberId: String,
    val name: String,
    val userName: String,
    val image: String,
    val status: String,
    val clubName: String
)

fun MemberDto.toUi() = MemberUiModel(
    memberId = memberId,
    name = name,
    userName = userName,
    image = image,
    status = status,
    clubName = clubName
)

