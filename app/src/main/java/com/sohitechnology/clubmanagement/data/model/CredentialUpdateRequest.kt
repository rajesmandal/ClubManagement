package com.sohitechnology.clubmanagement.data.model

data class CredentialUpdateRequest(
    val cId: Int,
    val id: Int,
    val password: String,
    val updateType: Int,  //0 for password change - 1 for username change - 2 for both change
    val userName: String
)