package com.sohitechnology.clubmanagement.ui.member

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sohitechnology.clubmanagement.data.model.AddMemberRequest
import com.sohitechnology.clubmanagement.ui.UiMessage
import com.sohitechnology.clubmanagement.ui.UiMessageType
import com.sohitechnology.clubmanagement.ui.common.CenterPopup
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun AddMemberScreen(
    viewModel: MemberViewModel,
    clubId: Int,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    AddMemberContent(
        state = state,
        eventFlow = viewModel.event,
        clubId = clubId,
        onAddMember = { viewModel.addMember(it) },
        onClearError = { viewModel.clearError() },
        onRefreshMembers = { viewModel.refreshMembers() },
        onBack = onBack,
        onSuccess = onSuccess
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberContent(
    state: MemberState,
    eventFlow: Flow<MemberUiEvent>,
    clubId: Int,
    onAddMember: (AddMemberRequest) -> Unit,
    onClearError: () -> Unit,
    onRefreshMembers: () -> Unit,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var contactNo by remember { mutableStateOf("") }
    var emailId by remember { mutableStateOf("") }
    var memberId by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    val genderOptions = listOf("Male", "Female", "Other")

    var showSuccessPopup by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var userNameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var contactError by remember { mutableStateOf<String?>(null) }
    var memberIdError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            if (event is MemberUiEvent.AddSuccess) {
                successMessage = event.message
                showSuccessPopup = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Member") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = memberId,
                    onValueChange = { memberId = it; memberIdError = null },
                    label = { Text("Member ID *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = memberIdError != null,
                    supportingText = { memberIdError?.let { Text(it) } }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it; userNameError = null },
                    label = { Text("Username *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = userNameError != null,
                    supportingText = { userNameError?.let { Text(it) } }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = null },
                    label = { Text("Password *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordError != null,
                    supportingText = { passwordError?.let { Text(it) } }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Gender *", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    genderOptions.forEachIndexed { _, label ->
                        // Weight(1f) ensures equal width for all 3 items
                        Box(modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)) {
                            StatusOption(
                                label = label,
                                selected = selectedGender == label,
                                onClick = { selectedGender = label }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contactNo,
                    onValueChange = { contactNo = it; contactError = null },
                    label = { Text("Contact No *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = contactError != null,
                    supportingText = { contactError?.let { Text(it) } }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = emailId,
                    onValueChange = { emailId = it },
                    label = { Text("Email ID (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        var isValid = true
                        if (name.isBlank()) { nameError = "Name is required"; isValid = false }
                        if (memberId.isBlank()) { memberIdError = "Member ID is required"; isValid = false }
                        if (userName.isBlank()) { userNameError = "Username is required"; isValid = false }
                        if (password.isBlank()) { passwordError = "Password is required"; isValid = false }
                        if (contactNo.isBlank()) { contactError = "Contact is required"; isValid = false }

                        if (isValid) {
                            onAddMember(
                                AddMemberRequest(
                                    clubId = clubId,
                                    contactNo = contactNo,
                                    emailId = emailId,
                                    gender = selectedGender,
                                    image = "",
                                    memberId = memberId,
                                    name = name,
                                    password = password,
                                    userName = userName
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Add Member")
                    }
                }
            }

            if (state.error != null) {
                CenterPopup(
                    uiMessage = UiMessage(
                        title = "Error",
                        message = state.error,
                        type = UiMessageType.ERROR
                    ),
                    onDismiss = { onClearError() }
                )
            }

            if (showSuccessPopup) {
                CenterPopup(
                    uiMessage = UiMessage(
                        title = "Success",
                        message = successMessage,
                        type = UiMessageType.INFO
                    ),
                    onDismiss = {
                        showSuccessPopup = false
                        onRefreshMembers()
                        onSuccess()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddMemberScreenPreview() {
    ClubManagementTheme {
        AddMemberContent(
            state = MemberState(),
            eventFlow = emptyFlow(),
            clubId = 1,
            onAddMember = {},
            onClearError = {},
            onRefreshMembers = {},
            onBack = {},
            onSuccess = {}
        )
    }
}
