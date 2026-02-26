package com.sohitechnology.clubmanagement.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sohitechnology.clubmanagement.R
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit = {}
) {
    // Use onSurface color from the theme scheme instead of checking isSystemInDarkTheme()
    val contentColor = MaterialTheme.colorScheme.onSurface

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
        },
        navigationIcon = {
            Row(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .clickable { onMenuClick() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu),
                    contentDescription = "Menu",
                    modifier = Modifier.size(18.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor
                )
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(40.dp)
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(20.dp),
                    tint = contentColor
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    ClubManagementTheme(darkTheme = false) {
        AppTopBar(title = "Home", onMenuClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun DarkAppTopBarPreview() {
    ClubManagementTheme(darkTheme = true) {
        AppTopBar(title = "Home", onMenuClick = {})
    }
}
