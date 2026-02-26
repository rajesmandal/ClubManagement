package com.sohitechnology.clubmanagement.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sohitechnology.clubmanagement.R
import com.sohitechnology.clubmanagement.core.NavigationEvent
import com.sohitechnology.clubmanagement.core.NavigationManager
import com.sohitechnology.clubmanagement.navigation.AppBottomBar
import com.sohitechnology.clubmanagement.ui.UiMessage
import com.sohitechnology.clubmanagement.ui.UiMessageType
import com.sohitechnology.clubmanagement.ui.auth.BiometricAuthenticator
import com.sohitechnology.clubmanagement.ui.common.AppTopBar
import com.sohitechnology.clubmanagement.ui.common.CenterPopup
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    onMenuClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
    biometricAuthenticator: BiometricAuthenticator // Should be injected or provided
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isAppLockEnabled by viewModel.isAppLockEnabled.collectAsState()
    val context = LocalContext.current
    
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showUpdateBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var showSuccessPopup by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.credentialUpdateSuccess.collect { message ->
            successMessage = message
            showSuccessPopup = true
            delay(2000)
            showSuccessPopup = false
            viewModel.logout() // Force logout after success
        }
    }

    LaunchedEffect(viewModel.logoutSuccess) {
        if (viewModel.logoutSuccess) {
            NavigationManager.navigate(NavigationEvent.ToLogin)
            viewModel.resetLogoutState()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "My Profile",
                onMenuClick = onMenuClick
            )
        },
        bottomBar = {
            AppBottomBar(navController)
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header Section
                ProfileHeader(userProfile)

                Spacer(modifier = Modifier.height(32.dp))

                // Account Information Section
                ProfileSectionTitle("Account Details")
                
                ProfileInfoCard {
                    ProfileInfoItem(
                        icon = Icons.Outlined.Person,
                        label = "Username",
                        value = userProfile.userName
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ProfileInfoItem(
                        icon = Icons.Outlined.Badge,
                        label = "Role",
                        value = userProfile.role.uppercase(),
                        valueColor = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ProfileInfoItem(
                        icon = Icons.Outlined.Business,
                        label = "Company ID",
                        value = userProfile.companyId
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Section
                ProfileSectionTitle("Settings")
                ProfileInfoCard {
                    // App Lock Option
                    ProfileSwitchItem(
                        icon = Icons.Outlined.Security,
                        label = "App Lock",
                        checked = isAppLockEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                // Trigger Biometric for turning ON
                                (context as? FragmentActivity)?.let { activity ->
                                    biometricAuthenticator.promptBiometric(
                                        activity = activity,
                                        title = "Enable App Lock",
                                        subtitle = "Authenticate to enable secure lock",
                                        onSuccess = { viewModel.setAppLockEnabled(true) },
                                        onError = { _, _ -> /* Reverts automatically since state didn't change */ },
                                        onFailed = { /* Reverts automatically */ }
                                    )
                                }
                            } else {
                                // Disable immediately
                                viewModel.setAppLockEnabled(false)
                            }
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ProfileActionItem(
                        icon = Icons.Outlined.Lock,
                        label = "Update Credentials",
                        onClick = { showUpdateBottomSheet = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ProfileActionItem(
                        icon = Icons.AutoMirrored.Outlined.Logout,
                        label = "Sign Out",
                        labelColor = MaterialTheme.colorScheme.error,
                        onClick = { showLogoutConfirm = true }
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }

            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            if (showLogoutConfirm) {
                CenterPopup(
                    uiMessage = UiMessage(
                        title = "Logout",
                        message = "Are you sure you want to sign out from your account?",
                        type = UiMessageType.INFO
                    ),
                    onDismiss = { showLogoutConfirm = false },
                    actionText = "Logout",
                    onAction = {
                        showLogoutConfirm = false
                        viewModel.logout()
                    }
                )
            }

            if (showSuccessPopup) {
                CenterPopup(
                    uiMessage = UiMessage(
                        title = "Success",
                        message = successMessage,
                        type = UiMessageType.INFO
                    ),
                    onDismiss = { },
                    autoDismissSeconds = 0
                )
            }

            if (showUpdateBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showUpdateBottomSheet = false },
                    sheetState = sheetState,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    UpdateCredentialsSheetContent(
                        onDismiss = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showUpdateBottomSheet = false
                            }
                        },
                        onUpdate = { password, userName, type ->
                            viewModel.updateCredentials(password, userName, type)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showUpdateBottomSheet = false
                            }
                        }
                    )
                }
            }
            
            if (viewModel.error != null) {
                CenterPopup(
                    uiMessage = UiMessage(
                        title = "Update Failed",
                        message = viewModel.error ?: "Unknown error",
                        type = UiMessageType.ERROR
                    ),
                    onDismiss = { viewModel.clearError() }
                )
            }
        }
    }
}

@Composable
fun ProfileSwitchItem(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            thumbContent = {
                Icon(
                    imageVector = if (checked) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
    }
}

@Composable
fun UpdateCredentialsSheetContent(
    onDismiss: () -> Unit,
    onUpdate: (String, String, Int) -> Unit
) {
    var updateType by remember { mutableIntStateOf(1) } // 0: Pwd, 1: User, 2: Both
    var newUserName by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Update Credentials",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "I want to update:", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Username" to 1, "Password" to 0, "Both" to 2).forEach { (label, type) ->
                FilterChip(
                    selected = updateType == type,
                    onClick = { updateType = type },
                    label = { Text(label) },
                    shape = RoundedCornerShape(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (updateType == 1 || updateType == 2) {
            OutlinedTextField(
                value = newUserName,
                onValueChange = { newUserName = it },
                label = { Text("New Username") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (updateType == 0 || updateType == 2) {
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        val isEnabled = when (updateType) {
            1 -> newUserName.isNotBlank()
            0 -> newPassword.isNotBlank()
            2 -> newUserName.isNotBlank() && newPassword.isNotBlank()
            else -> false
        }

        Button(
            onClick = { onUpdate(newPassword, newUserName, updateType) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = isEnabled
        ) {
            Text("Update Credentials", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileHeader(profile: UserProfile) {
    val context = LocalContext.current
    val baseUrl = "http://192.168.18.72:7001/"
    val imagePath = profile.profileImage
    val fullImageUrl = if (imagePath.isNotEmpty() && !imagePath.startsWith("http")) {
        baseUrl + imagePath.removePrefix("/")
    } else {
        imagePath
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(4.dp, MaterialTheme.colorScheme.background),
                shadowElevation = 8.dp
            ) {
                if (imagePath.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(fullImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = (profile.fullName.takeIf { it.isNotBlank() } ?: "U").take(1).uppercase(),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp,
                border = BorderStroke(2.dp, Color.White)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    modifier = Modifier.padding(6.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = profile.fullName.ifBlank { "User Name" },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            shape = RoundedCornerShape(100.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = profile.role.uppercase(),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    )
}

@Composable
fun ProfileInfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}

@Composable
fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        }
    }
}

@Composable
fun ProfileActionItem(
    icon: ImageVector,
    label: String,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(10.dp),
                tint = if (labelColor == MaterialTheme.colorScheme.error) labelColor else MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = labelColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
    }
}
