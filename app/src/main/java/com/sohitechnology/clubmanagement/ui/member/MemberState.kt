package com.sohitechnology.clubmanagement.ui.member

data class MemberState(
    val isLoading: Boolean = false,
    val members: List<MemberUiModel> = emptyList(),
    val error: String? = null
)
