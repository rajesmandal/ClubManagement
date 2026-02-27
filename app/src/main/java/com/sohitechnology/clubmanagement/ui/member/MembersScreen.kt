package com.sohitechnology.clubmanagement.ui.member

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sohitechnology.clubmanagement.navigation.AppBottomBar
import com.sohitechnology.clubmanagement.navigation.MainRoute
import com.sohitechnology.clubmanagement.ui.common.CenterPopup
import com.sohitechnology.clubmanagement.ui.UiMessage
import com.sohitechnology.clubmanagement.ui.UiMessageType
import com.sohitechnology.clubmanagement.ui.common.AppDropdown
import com.sohitechnology.clubmanagement.ui.common.AppTopBar
import com.sohitechnology.clubmanagement.ui.common.DropdownItem
import com.sohitechnology.clubmanagement.ui.common.EmptyState
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MembersScreen(
    navController: NavHostController,
    viewModel: MemberViewModel,
    onMemberClick: (MemberUiModel) -> Unit,
    onAddMemberClick: (Int) -> Unit,
    onMenuClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val state by viewModel.state.collectAsState()

    val viewModelMemberFilter: MemberFilterViewModel = hiltViewModel()
    val clubs by viewModelMemberFilter.clubs.collectAsState()

    MembersContent(
        state = state,
        clubs = clubs,
        navController = navController,
        onLoadMembers = { clubId, force -> viewModel.loadMembers(clubId, 0, force) },
        onRefresh = { clubId -> viewModel.loadMembers(clubId, 0, true) },
        onSelectMember = { viewModel.selectMember(it) },
        onClearError = { viewModel.clearError() },
        onMemberClick = onMemberClick,
        onAddMemberClick = onAddMemberClick,
        onMenuClick = onMenuClick,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MembersContent(
    state: MemberState,
    clubs: List<DropdownItem>,
    navController: NavHostController,
    onLoadMembers: (String, Boolean) -> Unit,
    onRefresh: (String) -> Unit,
    onSelectMember: (MemberUiModel) -> Unit,
    onClearError: () -> Unit,
    onMemberClick: (MemberUiModel) -> Unit,
    onAddMemberClick: (Int) -> Unit,
    onMenuClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var selectedClubId by remember { mutableStateOf(0) }
    var selectedStatus by remember { mutableStateOf(0) } // 0 = All, 1 = Active, 2 = Deactivated, 3 = Expired

    LaunchedEffect(Unit) {
        onLoadMembers(selectedClubId.toString(), false)
    }

    val filteredMembers = remember(state.members, selectedStatus) {
        if (selectedStatus == 0) {
            state.members
        } else {
            state.members.filter { member ->
                val status = member.status.lowercase()
                when (selectedStatus) {
                    1 -> status == "active"
                    2 -> status.contains("deactive") || status.contains("deactivated")
                    3 -> status == "expired"
                    else -> true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Members",
                onMenuClick = onMenuClick,
                onNotificationClick = {
                    navController.navigate(MainRoute.Notification.route)
                }
            )
        },
        bottomBar = {
            AppBottomBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddMemberClick(selectedClubId) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Member")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                AppDropdown(
                    label = "Select Club",
                    items = clubs,
                    selectedId = selectedClubId,
                    onItemSelected = {
                        if (selectedClubId != it.id) {
                            selectedClubId = it.id
                            onLoadMembers(it.id.toString(), true)
                        }
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val options = listOf("All", "Active", "De active", "Expired")
                options.forEachIndexed { index, label ->
                    // Weight(1f) ensures equal width for all 4 items
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)) {
                        StatusOption(
                            label = label,
                            selected = selectedStatus == index,
                            onClick = { selectedStatus = index }
                        )
                    }
                }
            }

            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = {
                    onRefresh(selectedClubId.toString())
                },
                modifier = Modifier.weight(1f)
            ) {
                when {
                    state.isLoading && state.members.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    state.error != null && state.members.isEmpty() -> {
                        CenterPopup(
                            uiMessage = UiMessage(
                                title = "Error",
                                message = state.error ?: "Something went wrong",
                                type = UiMessageType.ERROR
                            ),
                            onDismiss = {
                                onClearError()
                            }
                        )

                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No data available. Please refresh.")
                        }
                    }

                    else -> {
                        if (filteredMembers.isEmpty()) {
                            EmptyState(
                                title = "No Members Found",
                                description = "Try changing filters or refresh the list."
                            )
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(filteredMembers) { member ->
                                    MemberItem(
                                        member = member,
                                        onClick = {
                                            onSelectMember(member)
                                            onMemberClick(member)
                                        },
                                        sharedTransitionScope = sharedTransitionScope,
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                }
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
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray
        )
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall // Chota font taaki "Deactivated" fit ho jaye
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MembersScreenPreview() {
    ClubManagementTheme {
        Surface {
            SharedTransitionLayout {
                val navController = rememberNavController()
                val mockMembers = listOf(
                    MemberUiModel(
                        id = 1,
                        memberId = "M001",
                        name = "John Doe",
                        userName = "johndoe",
                        password = "123",
                        image = "",
                        status = "Active",
                        gender = "Male",
                        contactNo = "1234567890",
                        emailId = "john@example.com",
                        clubName = "Main Club",
                        clubId = 1,
                        birthDay = "1990-01-01",
                        hireDay = "2023-01-01",
                        address = "123 Street",
                        nationality = "Indian",
                        startDate = "2023-01-01",
                        expiryDate = "2024-01-01"
                    ),
                    MemberUiModel(
                        id = 2,
                        memberId = "M002",
                        name = "Jane Smith",
                        userName = "janesmith",
                        password = "123",
                        image = "",
                        status = "Expired",
                        gender = "Female",
                        contactNo = "0987654321",
                        emailId = "jane@example.com",
                        clubName = "Main Club",
                        clubId = 1,
                        birthDay = "1995-05-05",
                        hireDay = "2022-05-05",
                        address = "456 Avenue",
                        nationality = "Indian",
                        startDate = "2022-05-05",
                        expiryDate = "2023-05-05"
                    )
                )

                val mockClubs = listOf(
                    DropdownItem(1, "Main Club"),
                    DropdownItem(2, "Downtown Club")
                )

                AnimatedContent(targetState = true, label = "") { _ ->
                    MembersContent(
                        state = MemberState(members = mockMembers),
                        clubs = mockClubs,
                        navController = navController,
                        onLoadMembers = { _, _ -> },
                        onRefresh = { _ -> },
                        onSelectMember = {},
                        onClearError = {},
                        onMemberClick = {},
                        onAddMemberClick = {},
                        onMenuClick = {},
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent
                    )
                }
            }
        }
    }
}
