package com.sohitechnology.gymstudio.hammer.data.model

data class PackageResponse(
    val data: List<PackageDto>? = null,
    val error: Boolean? = null,
    val message: String? = null,
    val success: Boolean? = null
)
