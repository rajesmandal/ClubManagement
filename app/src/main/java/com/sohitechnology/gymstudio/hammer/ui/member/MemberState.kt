package com.sohitechnology.gymstudio.hammer.ui.member

data class MemberState(
    val isLoading: Boolean = false,
    val members: List<MemberUiModel> = emptyList(),
    val selectedMember: MemberUiModel? = null,
    val error: String? = null
)
