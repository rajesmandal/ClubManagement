package com.sohitechnology.gymstudio.hammer.ui

enum class UiMessageType {
    ERROR,
    INFO
}

data class UiMessage(
    val title: String,
    val message: String,
    val type: UiMessageType
)