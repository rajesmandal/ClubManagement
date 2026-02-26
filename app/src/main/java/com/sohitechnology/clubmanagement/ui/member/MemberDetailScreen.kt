package com.sohitechnology.clubmanagement.ui.member

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sohitechnology.clubmanagement.R
import com.sohitechnology.clubmanagement.data.cache.MemberCache
import com.sohitechnology.clubmanagement.data.model.UpdateMemberRequest
import com.sohitechnology.clubmanagement.ui.UiMessage
import com.sohitechnology.clubmanagement.ui.UiMessageType
import com.sohitechnology.clubmanagement.ui.common.CenterPopup
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MemberDetailScreen(
    viewModel: MemberViewModel,
    onBack: () -> Unit,
    onNavigateToMembers: () -> Unit,
    onRenew: (MemberUiModel) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val state by viewModel.state.collectAsState()
    val member = state.selectedMember

    var showSuccessPopup by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is MemberUiEvent.UpdateSuccess -> {
                    successMessage = event.message
                    showSuccessPopup = true
                }
                else -> Unit
            }
        }
    }

    MemberDetailContent(
        member = member,
        isLoading = state.isLoading,
        error = state.error,
        showSuccessPopup = showSuccessPopup,
        successMessage = successMessage,
        onUpdateMember = { viewModel.updateMember(it) },
        onBack = onBack,
        onNavigateToMembers = onNavigateToMembers,
        onRenew = onRenew,
        onClearError = { viewModel.clearError() },
        onRefreshMembers = { viewModel.refreshMembers() },
        onSuccessPopupDismiss = {
            showSuccessPopup = false
            MemberCache.clear()
            viewModel.refreshMembers()
            onBack() // Navigate back after success
        },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MemberDetailContent(
    member: MemberUiModel?,
    isLoading: Boolean,
    error: String?,
    showSuccessPopup: Boolean,
    successMessage: String,
    onUpdateMember: (UpdateMemberRequest) -> Unit,
    onBack: () -> Unit,
    onNavigateToMembers: () -> Unit,
    onRenew: (MemberUiModel) -> Unit,
    onClearError: () -> Unit,
    onRefreshMembers: () -> Unit,
    onSuccessPopupDismiss: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var isEditMode by remember { mutableStateOf(false) }

    // Editable fields
    var name by remember(member) { mutableStateOf(member?.name ?: "") }
    var contactNo by remember(member) { mutableStateOf(member?.contactNo ?: "") }
    var emailId by remember(member) { mutableStateOf(member?.emailId ?: "") }
    var address by remember(member) { mutableStateOf(member?.address ?: "") }
    var gender by remember(member) { mutableStateOf(member?.gender ?: "Male") }
    var birthDay by remember(member) { mutableStateOf(TextFieldValue(member?.birthDay ?: "")) }
    var hireDay by remember(member) { mutableStateOf(member?.hireDay ?: "") }
    var nationality by remember(member) { mutableStateOf(member?.nationality ?: "") }
    var startDate by remember(member) { mutableStateOf(member?.startDate ?: "") }
    var expiryDate by remember(member) { mutableStateOf(member?.expiryDate ?: "") }

    val countries = remember {
        listOf("Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, North", "Korea, South", "Kosovo", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Macedonia", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe").sorted()
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isEditMode) "Edit Profile" else "Profile Details", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { if (isEditMode) isEditMode = false else onBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (!isEditMode && member != null) {
                            Surface(
                                modifier = Modifier.padding(end = 12.dp).size(40.dp),
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            ) {
                                IconButton(onClick = { isEditMode = true }) {
                                    Icon(
                                        Icons.Outlined.Edit,
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (member != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .navigationBarsPadding()
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (isEditMode) {
                                OutlinedButton(
                                    onClick = { isEditMode = false },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(50.dp),
                                    contentPadding = PaddingValues(vertical = 12.dp)
                                ) {
                                    Text("Cancel")
                                }
                                Button(
                                    onClick = {
                                        if (isValidEmail(emailId) && contactNo.length == 10) {
                                            onUpdateMember(
                                                UpdateMemberRequest(
                                                    id = member.id,
                                                    memberId = member.memberId,
                                                    name = name,
                                                    userName = member.userName,
                                                    password = member.password,
                                                    image = member.image,
                                                    status = member.status,
                                                    gender = gender,
                                                    contactNo = contactNo,
                                                    emailId = emailId,
                                                    clubName = member.clubName,
                                                    clubId = member.clubId,
                                                    birthDay = convertToApiDate(birthDay.text),
                                                    hireDay = hireDay,
                                                    address = address,
                                                    nationality = nationality,
                                                    startDate = startDate,
                                                    expiryDate = expiryDate
                                                )
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(50.dp),
                                    contentPadding = PaddingValues(vertical = 12.dp),
                                    enabled = !isLoading && isValidEmail(emailId) && contactNo.length == 10
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                                    } else {
                                        Text("Save Changes")
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { onRenew(member) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(50.dp),
                                    contentPadding = PaddingValues(vertical = 12.dp)
                                ) {
                                    Text("Renew Membership")
                                }
                            }
                        }
                    }
                }
            }
        ) { padding ->
            member?.let {
                Box(modifier = Modifier.padding(padding)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Card Section
                        MemberHeaderCard(it, animatedVisibilityScope, sharedTransitionScope)

                        Spacer(modifier = Modifier.height(24.dp))

                        if (isEditMode) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Full Name") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Category: Personal Info
                        DetailCategoryCard(title = "Personal Information") {
                            DetailRowItem(
                                icon = Icons.Default.Person,
                                label = "Gender",
                                value = gender,
                                isEditable = isEditMode,
                                isGenderSelector = true,
                                onValueChange = { gender = it }
                            )
                            DetailRowItem(
                                icon = Icons.Default.CalendarMonth,
                                label = "Birthday",
                                textFieldValue = birthDay,
                                isEditable = isEditMode,
                                isDatePicker = true,
                                onTextFieldValueChange = { birthDay = formatBirthday(it) }
                            )
                            DetailRowItem(
                                icon = Icons.Default.Public,
                                label = "Country",
                                value = nationality,
                                isEditable = isEditMode,
                                isCountrySelector = true,
                                countries = countries,
                                onValueChange = { nationality = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category: Contact Info
                        DetailCategoryCard(title = "Contact Information") {
                            DetailRowItem(
                                icon = Icons.Default.Phone,
                                label = "Phone Number",
                                value = contactNo,
                                isEditable = isEditMode,
                                isPhoneNumber = true,
                                onValueChange = { if (it.length <= 10) contactNo = it }
                            )
                            DetailRowItem(
                                icon = Icons.Default.Email,
                                label = "Email Address",
                                value = emailId,
                                isEditable = isEditMode,
                                isEmail = true,
                                errorText = if (!isValidEmail(emailId) && emailId.isNotEmpty()) "Invalid email format" else null,
                                onValueChange = { emailId = it }
                            )
                            DetailRowItem(
                                icon = Icons.Default.LocationOn,
                                label = "Home Address",
                                value = address,
                                isEditable = isEditMode,
                                onValueChange = { address = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category: Membership Info
                        DetailCategoryCard(title = "Membership Details") {
                            DetailRowItem(
                                icon = Icons.Default.CardMembership,
                                label = "Member ID",
                                value = it.memberId,
                                isEditable = false
                            )
                            DetailRowItem(
                                icon = Icons.Default.CalendarMonth,
                                label = "Joining Date",
                                value = it.startDate,
                                isEditable = false
                            )
                            DetailRowItem(
                                icon = Icons.Default.CalendarMonth,
                                label = "Expiry Date",
                                value = it.expiryDate,
                                isEditable = false
                            )
                            DetailRowItem(
                                icon = Icons.Default.LocationOn,
                                label = "Assigned Club",
                                value = it.clubName,
                                isEditable = false
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    if (error != null) {
                        CenterPopup(
                            uiMessage = UiMessage(
                                title = "Error",
                                message = error,
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
                                onSuccessPopupDismiss()
                            }
                        )
                    }
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Member not found")
            }
        }
    }
}

private fun formatBirthday(value: TextFieldValue): TextFieldValue {
    val text = value.text.filter { it.isDigit() }
    val formatted = StringBuilder()

    for (i in text.indices) {
        formatted.append(text[i])
        if ((i == 1 || i == 3) && i != text.lastIndex) {
            formatted.append("-")
        }
    }

    val resultText = formatted.toString().take(10) // Limit to DD-MM-YYYY

    // Calculate new cursor position
    var selectionIndex = value.selection.end
    
    // If a hyphen was just auto-inserted, move cursor past it
    if (resultText.length > value.text.length) {
        if (resultText.length == 3 || resultText.length == 6) {
            selectionIndex = resultText.length
        }
    }

    return TextFieldValue(
        text = resultText,
        selection = TextRange(selectionIndex.coerceIn(0, resultText.length))
    )
}

private fun convertToApiDate(displayDate: String): String {
    return try {
        // Assuming user enters DD-MM-YYYY
        val parts = displayDate.split("-")
        if (parts.size == 3) {
            "${parts[2]}/${parts[1]}/${parts[0]}" // Returns YYYY/MM/DD
        } else displayDate
    } catch (e: Exception) {
        displayDate
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MemberHeaderCard(
    member: MemberUiModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {
    val context = LocalContext.current
    val baseUrl = "http://192.168.18.72:7001/"
    val imagePath = member.image
    val fullImageUrl = if (imagePath.isNotEmpty() && !imagePath.startsWith("http")) {
        baseUrl + imagePath.removePrefix("/")
    } else {
        imagePath
    }

    with(sharedTransitionScope) {
        Card(
            modifier = Modifier.fillMaxWidth().sharedBounds(
                rememberSharedContentState(key = "card-${member.memberId}"),
                animatedVisibilityScope = animatedVisibilityScope
            ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Picture (Round)
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(4.dp, MaterialTheme.colorScheme.surface),
                    shadowElevation = 8.dp
                ) {
                    if (imagePath.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(fullImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = (member.name.takeIf { it.isNotBlank() } ?: "U").take(1).uppercase(),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.height(80.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = member.name.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // Status Chip
                            val status = member.status.lowercase()
                            val statusColor = when (status) {
                                "active" -> Color(0xFF10B981)
                                "expired" -> Color(0xFFEF4444)
                                else -> Color(0xFF6B7280)
                            }

                            Surface(
                                color = statusColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = status.uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = statusColor
                                )
                            }
                        }
                        Text(
                            text = "@${member.userName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // ID Badge
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ID: ${member.memberId}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Club Name
                        Text(
                            text = member.clubName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailCategoryCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailRowItem(
    icon: ImageVector,
    label: String,
    value: String = "",
    textFieldValue: TextFieldValue = TextFieldValue(""),
    isEditable: Boolean = false,
    isGenderSelector: Boolean = false,
    isDatePicker: Boolean = false,
    isCountrySelector: Boolean = false,
    isPhoneNumber: Boolean = false,
    isEmail: Boolean = false,
    countries: List<String> = emptyList(),
    errorText: String? = null,
    onValueChange: (String) -> Unit = {},
    onTextFieldValueChange: (TextFieldValue) -> Unit = {}
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredCountries = remember(searchQuery, countries) {
        if (searchQuery.isEmpty()) countries 
        else countries.filter { it.contains(searchQuery, ignoreCase = true) }
    }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isSearchEnabled by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            if (isEditable) {
                when {
                    isGenderSelector -> {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Male", "Female", "Other").forEach { option ->
                                FilterChip(
                                    selected = value == option,
                                    onClick = { onValueChange(option) },
                                    label = { Text(option) },
                                    shape = RoundedCornerShape(50.dp)
                                )
                            }
                        }
                    }
                    isDatePicker -> {
                        OutlinedTextField(
                            value = textFieldValue,
                            onValueChange = onTextFieldValueChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("DD-MM-YYYY") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = {
                                IconButton(onClick = {
                                    val calendar = Calendar.getInstance()
                                    DatePickerDialog(context, { _, y, m, d ->
                                        val cal = Calendar.getInstance()
                                        cal.set(y, m, d)
                                        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                        onTextFieldValueChange(TextFieldValue(sdf.format(cal.time), selection = TextRange(10)))
                                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                                }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Calendar")
                                }
                            }
                        )
                    }
                    isCountrySelector -> {
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        
                        LaunchedEffect(isPressed) {
                            if (isPressed) {
                                if (expanded) {
                                    isSearchEnabled = true
                                } else {
                                    expanded = true
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { 
                                if (!it) {
                                    isSearchEnabled = false
                                    searchQuery = ""
                                }
                                expanded = it 
                            }
                        ) {
                            val displayValue = remember(value, isSearchEnabled, searchQuery) {
                                if (isSearchEnabled) searchQuery else value
                            }
                            
                            OutlinedTextField(
                                value = displayValue,
                                onValueChange = { 
                                    if (isSearchEnabled) {
                                        searchQuery = it
                                        onValueChange(it)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .focusRequester(focusRequester),
                                shape = RoundedCornerShape(8.dp),
                                readOnly = !isSearchEnabled,
                                interactionSource = interactionSource,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { 
                                    focusManager.clearFocus()
                                    expanded = false
                                    isSearchEnabled = false
                                })
                            )
                            
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { 
                                    expanded = false
                                    isSearchEnabled = false
                                }
                            ) {
                                filteredCountries.forEach { country ->
                                    DropdownMenuItem(
                                        text = { Text(country) },
                                        onClick = {
                                            onValueChange(country)
                                            expanded = false
                                            isSearchEnabled = false
                                            focusManager.clearFocus()
                                        }
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        OutlinedTextField(
                            value = value,
                            onValueChange = onValueChange,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = if (isPhoneNumber) KeyboardType.Number else if (isEmail) KeyboardType.Email else KeyboardType.Text
                            ),
                            shape = RoundedCornerShape(8.dp),
                            isError = errorText != null,
                            supportingText = { errorText?.let { Text(it) } }
                        )
                    }
                }
            } else {
                Text(
                    text = value.ifBlank { "Not Provided" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun MemberDetailScreenPreview() {
    val mockMember = MemberUiModel(
        id = 1,
        memberId = "M001",
        name = "Johnathan Doe",
        userName = "johndoe",
        password = "",
        image = "",
        status = "Active",
        gender = "Male",
        contactNo = "1234567890",
        emailId = "john@example.com",
        clubName = "Fitness Elite Club",
        clubId = 1,
        birthDay = "01-01-1990",
        hireDay = "01-01-2023",
        address = "123 Main Street",
        nationality = "Indian",
        startDate = "01-01-2024",
        expiryDate = "01-01-2025"
    )

    ClubManagementTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                MemberDetailContent(
                    member = mockMember,
                    isLoading = false,
                    error = null,
                    showSuccessPopup = false,
                    successMessage = "",
                    onUpdateMember = {},
                    onBack = {},
                    onNavigateToMembers = {},
                    onRenew = {},
                    onClearError = {},
                    onRefreshMembers = {},
                    onSuccessPopupDismiss = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility
                )
            }
        }
    }
}
