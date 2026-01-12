package com.sohitechnology.clubmanagement.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sohitechnology.clubmanagement.ui.common.CenterPopup
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
){
    val state by viewModel.state.collectAsState()

    LoginScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        clearUiMessage = viewModel::clearUiMessage
    )
}

@Composable
fun LoginScreenContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    clearUiMessage: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Club Management",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Login",
                style = MaterialTheme.typography.bodyMedium ,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Company Id
            OutlinedTextField(
                value = state.companyId,
                onValueChange = {
                    onEvent(LoginEvent.CompanyIdChanged(it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Company Id") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Business, // Aap Badge ya Apartment bhi use kar sakte hain
                        contentDescription = null
                    )},
                singleLine = true,
                isError = state.companyIdError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            // Company Id error text
            if (state.companyIdError != null) {
                Text(
                    text = state.companyIdError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Email / Username
            OutlinedTextField(
                value = state.username,
                onValueChange = {
                    onEvent(LoginEvent.EmailChanged(it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                singleLine = true,
                isError = state.usernameError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            // Email error text
            if (state.usernameError != null) {
                Text(
                    text = state.usernameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Password
            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    onEvent(LoginEvent.PasswordChanged(it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                singleLine = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                isError = state.passwordError != null
            )

            // Password Error text
            if (state.passwordError != null) {
                Text(
                    text = state.passwordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Login Button
            Button(
                onClick = {
                    onEvent(LoginEvent.LoginClicked)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = state.isLoginEnabled
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            //Error Message Show
            state.uiMessage?.let { message ->
                CenterPopup(
                    uiMessage = message,
                    onDismiss = {
                        clearUiMessage()
                    }
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ClubManagementTheme(darkTheme = false) {
        LoginScreenContent(
            state = LoginState(companyId = "123456", username = "test@example.com"), // Dummy state
            onEvent = {},
            clearUiMessage = {}
        )
    }
}