package com.sohitechnology.clubmanagement.ui.member

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sohitechnology.clubmanagement.ui.common.CenterPopup
import com.sohitechnology.clubmanagement.ui.UiMessage
import com.sohitechnology.clubmanagement.ui.UiMessageType
import com.sohitechnology.clubmanagement.ui.common.AppDropdown
import com.sohitechnology.clubmanagement.ui.common.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(
    viewModel: MemberViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val viewModelMemberFilter: MemberFilterViewModel = hiltViewModel()
    val clubs by viewModelMemberFilter.clubs.collectAsState()

    var selectedClubId by remember { mutableStateOf(0) }
    var selectedStatus by remember { mutableStateOf(0) } // 0 = All, 1 = Active, 2 = Deactivated, 3 = Expired

    LaunchedEffect(selectedClubId, selectedStatus) {
        // Condition: Agar members ki list khali hai (first time)
        // YA user ne explicitly filter change kiya hai (selectedStatus != 0 ya clubId != 0)
        if (state.members.isEmpty() || selectedStatus != 0 || selectedClubId != 0) {
            viewModel.loadMembers(selectedClubId.toString(), selectedStatus, forceRefresh = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.padding(16.dp)) {
            AppDropdown(
                label = "Select Club",
                items = clubs,
                selectedId = selectedClubId,
                onItemSelected = {
                    selectedClubId = it.id // expose selected id
                }
            )
        }

        // Status Filter Radio Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // weight(1f) hata diya
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Sabko barabar gap pe rakhega
        ) {
            // Ek helper function use kar sakte hain ya direct niche wala repeat karein
            StatusOption(
                label = "All",
                selected = selectedStatus == 0,
                onClick = { selectedStatus = 0 }
            )
            StatusOption(
                label = "Active",
                selected = selectedStatus == 1,
                onClick = { selectedStatus = 1 }
            )
            StatusOption(
                label = "Deactivated",
                selected = selectedStatus == 2,
                onClick = { selectedStatus = 2 }
            )
            StatusOption(
                label = "Expired",
                selected = selectedStatus == 3,
                onClick = { selectedStatus = 3 }
            )
        }


        // 1. PullToRefreshBox sabse aasan aur modern tarika hai
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = {
                viewModel.loadMembers(
                    selectedClubId.toString(),
                    selectedStatus,
                    forceRefresh = true
                )
            },
            modifier = Modifier.weight(1f)
        ) {
            when {
                // Sirf tab loader dikhayein jab pehli baar data load ho raha ho
                state.isLoading && state.members.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null && state.members.isEmpty() -> {
                    // Jab error null nahi hoga, tab ye popup dikhega
                    CenterPopup(
                        uiMessage = UiMessage(
                            title = "Error",
                            message = state.error ?: "Something went wrong",
                            type = UiMessageType.ERROR
                        ),
                        onDismiss = {
                            viewModel.clearError() // Isse state.error null ho jayega aur popup hat jayega
                        }
                    )

                    // Agar aap chahte hain ki popup ke peeche screen khali na dikhe,
                    // toh yahan ek retry button ya placeholder text bhi dal sakte hain.
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No data available. Please refresh.")
                    }
                }

                else -> {
                    if (state.members.isEmpty()) {
                        // --- EMPTY DATA VIEW START ---
                        EmptyState(
                            title = "No Members Found",description = "Try changing filters or refresh the list."
                        )
                        // --- EMPTY DATA VIEW END ---
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.members) { member ->
                                MemberItem(member = member)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun StatusOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            modifier = Modifier.size(32.dp) // Size chota kiya taaki sab fit ho jayein
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium, // Font size thoda chota
            modifier = Modifier.padding(start = 2.dp, end = 4.dp)
        )
    }
}
