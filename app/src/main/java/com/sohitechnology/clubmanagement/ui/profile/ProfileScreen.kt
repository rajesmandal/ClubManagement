package com.sohitechnology.clubmanagement.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
)
{
    Box(Modifier.fillMaxSize().clickable(onClick = {viewModel.logout()}), contentAlignment = Alignment.Center) {
        Text("Profile Screen")
    }
}

