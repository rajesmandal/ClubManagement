package com.sohitechnology.gymstudio.hammer.data.model

data class ImageUploadRequest(
    val cId: Int,
    val folderName: String,
    val base64Strings: String,
    val fileName: String,
    val fileType: String,
    val imgKey : String
)

data class ImageUploadResponse(
    val success : Boolean? = null,
    val message: String? = null,
    val error: Boolean? = null,
    val `data`: ImageData,
)

data class ImageData(
    val imageName: String
)