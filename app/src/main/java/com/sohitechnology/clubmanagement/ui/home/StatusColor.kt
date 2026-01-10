package com.sohitechnology.clubmanagement.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sohitechnology.clubmanagement.ui.home.model.ClientStatus

@Composable
fun statusColor(status: ClientStatus) = when (status) {
    ClientStatus.ACTIVE -> Color(0xFF16A34A)       // green
    ClientStatus.DEACTIVATED -> Color(0xFFDC2626)  // red
    ClientStatus.EXPIRED -> Color(0xFFF59E0B)      // orange
}
