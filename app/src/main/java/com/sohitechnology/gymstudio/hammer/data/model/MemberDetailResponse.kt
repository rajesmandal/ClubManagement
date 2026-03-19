package com.sohitechnology.gymstudio.hammer.data.model

data class MemberDetailResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val error: Boolean? = null,
    val data: List<MemberDetailData>? = null
)

data class MemberDetailData(
    val id: Int? = null,
    val memberId: String? = null,
    val name: String? = null,
    val clubId: Int? = null,
    val clubName: String? = null,
    val contactNo: String? = null,
    val emailId: String? = null,
    val gender: String? = null,
    val userName: String? = null,
    val image: String? = null,
    val status: String? = null,
    val birthDay: String? = null,
    val hireDay: String? = null,
    val nationality: String? = null,
    val address: String? = null,
    val startDate: String? = null,
    val expiryDate: String? = null,
    val getMemberTransaction: List<MemberTransaction>? = null
)

data class MemberTransaction(
    val planName: String? = null,
    val validity: String? = null,
    val validityType: String? = null,
    val price: Int? = null,
    val startDate: String? = null,
    val expiryDate: String? = null
)
