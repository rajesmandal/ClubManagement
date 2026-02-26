package com.sohitechnology.clubmanagement.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sohitechnology.clubmanagement.R
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel()
) {
    // We call SplashScreenContent here. The viewModel is injected to trigger 
    // its init block logic for navigation.
    SplashScreenContent()
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp) // Adjust the size as per requirement
            )

            Spacer(modifier = Modifier.height(
                16.dp
            ))

            Text(
                text = "Club Management",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }
    }
}

// Preview Function
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    ClubManagementTheme {
        // Use SplashScreenContent in Preview to avoid ViewModel instantiation issues
        SplashScreenContent()
    }
}
