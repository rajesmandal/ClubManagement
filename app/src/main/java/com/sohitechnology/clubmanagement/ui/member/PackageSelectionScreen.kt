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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sohitechnology.clubmanagement.data.model.PackageDto
import com.sohitechnology.clubmanagement.ui.UiMessage
import com.sohitechnology.clubmanagement.ui.UiMessageType
import com.sohitechnology.clubmanagement.ui.common.CenterPopup
import com.sohitechnology.clubmanagement.ui.common.EmptyState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageSelectionScreen(
    viewModel: PackageViewModel,
    memberId: String,
    memberExpiryDate: String,
    onBack: () -> Unit,
    onRenewSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSuccessPopup by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            if (event is PackageUiEvent.RenewSuccess) {
                successMessage = event.message
                showSuccessPopup = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Package") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading && state.packages.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.packages.isEmpty() && !state.isLoading -> {
                    EmptyState(title = "No Packages Available", description = "Please contact admin.")
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.packages) { pkg ->
                            PackageItem(
                                pkg = pkg,
                                isSelected = state.selectedPackage == pkg,
                                onSelect = {
                                    viewModel.selectPackage(pkg)
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }
            }

            if (showBottomSheet && state.selectedPackage != null) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
                    RenewalSummary(
                        pkg = state.selectedPackage!!,
                        currentExpiry = memberExpiryDate,
                        isLoading = state.isLoading,
                        onConfirm = {
                            viewModel.renewMember(memberId)
                        }
                    )
                }
            }

            if (state.error != null) {
                CenterPopup(
                    uiMessage = UiMessage(
                        title = "Error",
                        message = state.error ?: "Something went wrong",
                        type = UiMessageType.ERROR
                    ),
                    onDismiss = { viewModel.clearError() }
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
                        onRenewSuccess()
                    },
                    autoDismissSeconds = 4 // No timer
                )
            }
        }
    }
}

@Composable
fun PackageItem(
    pkg: PackageDto,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onSelect() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pkg.name ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${pkg.type ?: ""} • ${pkg.validation ?: 0} Days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Text(
                text = "₹${pkg.price ?: 0}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RenewalSummary(
    pkg: PackageDto,
    currentExpiry: String,
    isLoading: Boolean,
    onConfirm: () -> Unit
) {
    val newExpiry = calculateNewExpiryDate(currentExpiry, pkg.validation ?: 0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Renewal Summary",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        SummaryRow(label = "Selected Package", value = pkg.name ?: "")
        SummaryRow(label = "Price", value = "₹${pkg.price ?: 0}")
        SummaryRow(label = "Current Expiry", value = currentExpiry)
        SummaryRow(label = "New Expiry", value = newExpiry, valueColor = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Confirm Renewal")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

fun calculateNewExpiryDate(currentExpiry: String, validationDays: Int): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return try {
        val date = sdf.parse(currentExpiry) ?: Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, validationDays)
        sdf.format(calendar.time)
    } catch (e: Exception) {
        "N/A"
    }
}
