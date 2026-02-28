package com.sohitechnology.gymstudio.hammer.data.model

data class CredentialUpdateResponse(
    val `data`: List<Any>,
    val error: Boolean,
    val message: String,
    val success: Boolean
)