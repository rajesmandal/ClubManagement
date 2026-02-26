package com.sohitechnology.clubmanagement.ui.common

import com.sohitechnology.clubmanagement.data.model.ClubDto

data class DropdownItem(
    val id: Int,
    val label: String
)

fun ClubDto.toDropdownItem() = DropdownItem(
    id = id?.toIntOrNull() ?: 0,
    label = name ?: ""
)
