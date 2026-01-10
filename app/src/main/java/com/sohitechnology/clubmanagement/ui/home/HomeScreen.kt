package com.sohitechnology.clubmanagement.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sohitechnology.clubmanagement.ui.home.model.ClientStatus
import com.sohitechnology.clubmanagement.ui.home.model.ClientUiModel

@Composable
fun HomeScreen() {

    // dummy list for UI only
    val clients = listOf(
        ClientUiModel("Rahul Sharma", ClientStatus.ACTIVE),
        ClientUiModel("Amit Verma", ClientStatus.EXPIRED),
        ClientUiModel("Neha Singh", ClientStatus.DEACTIVATED)
    )


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                WindowInsets.systemBars
                    .asPaddingValues()
            ),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(clients) { client ->
            ClientItem(client)
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}
