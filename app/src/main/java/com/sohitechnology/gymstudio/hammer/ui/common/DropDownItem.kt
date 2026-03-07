package com.sohitechnology.gymstudio.hammer.ui.common

import com.sohitechnology.gymstudio.hammer.data.model.ClubDto

data class DropdownItem(
    val id: Int,
    val label: String
)

fun ClubDto.toDropdownItem() = DropdownItem(
    id = id ?: 0,
    label = name ?: ""
)
