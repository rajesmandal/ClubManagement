package com.sohitechnology.clubmanagement.data.model

data class MemberCountResponse(
    val data: MemberCountData? = null,
    val error: Boolean? = null,
    val message: String? = null,
    val success: Boolean? = null
)

data class MemberCountData(
    val active: Int? = null,
    val all: Int? = null,
    val deactive: Int? = null,
    val expired: Int? = null
)
