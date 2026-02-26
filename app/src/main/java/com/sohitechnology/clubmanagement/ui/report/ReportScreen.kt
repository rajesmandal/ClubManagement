package com.sohitechnology.clubmanagement.ui.report

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.sohitechnology.clubmanagement.R
import com.sohitechnology.clubmanagement.data.model.ReportData
import com.sohitechnology.clubmanagement.data.model.TransactionData
import com.sohitechnology.clubmanagement.navigation.AppBottomBar
import com.sohitechnology.clubmanagement.ui.common.AppDropdown
import com.sohitechnology.clubmanagement.ui.common.AppTopBar
import com.sohitechnology.clubmanagement.ui.common.DropdownItem
import com.sohitechnology.clubmanagement.ui.common.EmptyState
import com.sohitechnology.clubmanagement.ui.member.MemberFilterViewModel
import com.sohitechnology.clubmanagement.ui.member.MemberUiModel
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportTabScreen(
    navController: NavHostController,
    onMenuClick: () -> Unit = {},
    viewModel: ReportViewModel = hiltViewModel(),
    filterViewModel: MemberFilterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val clubs by filterViewModel.clubs.collectAsState()
    
    ReportTabContent(
        state = state,
        clubs = clubs,
        navController = navController,
        onMenuClick = onMenuClick,
        onLoadMembers = { viewModel.loadMembers(it) },
        onGetReports = { clubId, memberIds, start, end ->
            viewModel.getReports(clubId, memberIds, start, end)
        },
        onGetTransactions = { viewModel.getTransactions(it) }
    )
}

@Composable
fun ReportTabContent(
    state: ReportState,
    clubs: List<DropdownItem>,
    navController: NavHostController,
    onMenuClick: () -> Unit,
    onLoadMembers: (Int) -> Unit,
    onGetReports: (String, String, String, String) -> Unit,
    onGetTransactions: (Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Report", "Transaction")

    // Initial Data Load
    LaunchedEffect(Unit) {
        onLoadMembers(0)
        onGetReports("0", "0", state.startDate, state.endDate)
        onGetTransactions(0)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Reports",
                onMenuClick = onMenuClick
            )
        },
        bottomBar = {
            AppBottomBar(navController)
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> ReportScreenContent(
                    state = state,
                    clubs = clubs,
                    onLoadMembers = onLoadMembers,
                    onGetReports = onGetReports
                )
                1 -> TransactionScreenContent(
                    state = state,
                    clubs = clubs,
                    onLoadMembers = onLoadMembers,
                    onGetTransactions = onGetTransactions
                )
            }
        }
    }
}

@Composable
fun ReportScreen(
    viewModel: ReportViewModel,
    filterViewModel: MemberFilterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val clubs by filterViewModel.clubs.collectAsState()
    ReportScreenContent(
        state = state,
        clubs = clubs,
        onLoadMembers = { viewModel.loadMembers(it) },
        onGetReports = { clubId, memberIds, start, end ->
            viewModel.getReports(clubId, memberIds, start, end)
        }
    )
}

@Composable
fun ReportScreenContent(
    state: ReportState,
    clubs: List<DropdownItem>,
    onLoadMembers: (Int) -> Unit,
    onGetReports: (String, String, String, String) -> Unit
) {
    var showFilterDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.reports.isEmpty()) {
                EmptyState(title = "No Reports Found", description = "Apply filters to see check-in/out reports.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(state.reports) { report ->
                        ReportItem(report)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showFilterDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter")
        }
    }

    if (showFilterDialog) {
        ReportFilterDialog(
            clubs = clubs,
            members = state.members,
            initialClubId = state.selectedClubId,
            initialMemberIds = state.selectedMemberIds,
            initialStartDate = state.startDate,
            initialEndDate = state.endDate,
            onDismiss = { showFilterDialog = false },
            onClubSelected = { clubId ->
                onLoadMembers(clubId)
            },
            onApply = { clubId, memberIds, start, end ->
                val memberIdsParam = if (memberIds.contains("0") || memberIds.isEmpty()) "0" else memberIds.joinToString(",")
                onGetReports(
                    clubId.toString(),
                    memberIdsParam,
                    start,
                    end
                )
                showFilterDialog = false
            }
        )
    }
}

@Composable
fun TransactionScreen(
    viewModel: ReportViewModel,
    filterViewModel: MemberFilterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val clubs by filterViewModel.clubs.collectAsState()
    TransactionScreenContent(
        state = state,
        clubs = clubs,
        onLoadMembers = { viewModel.loadMembers(it) },
        onGetTransactions = { viewModel.getTransactions(it) }
    )
}

@Composable
fun TransactionScreenContent(
    state: ReportState,
    clubs: List<DropdownItem>,
    onLoadMembers: (Int) -> Unit,
    onGetTransactions: (Int) -> Unit
) {
    var showFilterDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (state.transactions.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Transactions", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            Text("${state.totalCount}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Revenue", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                            Text("₹${state.totalAmount}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                        }
                    }
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.transactions.isEmpty()) {
                EmptyState(title = "No Transactions Found", description = "Select a member to see transaction history.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(state.transactions) { transaction ->
                        TransactionItem(transaction)
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showFilterDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter")
        }
    }

    if (showFilterDialog) {
        TransactionFilterDialog(
            clubs = clubs,
            members = state.members,
            initialClubId = state.selectedClubId,
            initialMemberId = state.selectedTransactionMemberId,
            onDismiss = { showFilterDialog = false },
            onClubSelected = { clubId ->
                onLoadMembers(clubId)
            },
            onApply = { memberId ->
                onGetTransactions(memberId)
                showFilterDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFilterDialog(
    clubs: List<DropdownItem>,
    members: List<MemberUiModel>,
    initialClubId: Int = 0,
    initialMemberIds: Set<String> = emptySet(),
    initialStartDate: String,
    initialEndDate: String,
    onDismiss: () -> Unit,
    onClubSelected: (Int) -> Unit,
    onApply: (Int, Set<String>, String, String) -> Unit
) {
    var clubId by remember { mutableIntStateOf(initialClubId) }
    var memberIds by remember { mutableStateOf(initialMemberIds) }
    var start by remember { mutableStateOf(initialStartDate) }
    var end by remember { mutableStateOf(initialEndDate) }
    var isMemberDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onClubSelected(clubId)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Report Filters", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))

                AppDropdown(
                    label = "Select Club",
                    items = clubs,
                    selectedId = clubId,
                    onItemSelected = {
                        clubId = it.id
                        onClubSelected(it.id)
                    }
                )

                Spacer(Modifier.height(12.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick = { isMemberDropdownExpanded = !isMemberDropdownExpanded },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val summaryText = when {
                                memberIds.contains("0") -> "All Members Selected"
                                memberIds.isEmpty() -> "Select Members"
                                else -> "${memberIds.size} Members Selected"
                            }
                            Text(
                                summaryText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(
                                if (isMemberDropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (isMemberDropdownExpanded) {
                    Card(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).padding(top = 4.dp).border(0.5.dp, Color.LightGray.copy(alpha = 0.5f), MaterialTheme.shapes.small),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        LazyColumn {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        memberIds = if (memberIds.contains("0")) emptySet() else setOf("0")
                                    }.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(checked = memberIds.contains("0"), onCheckedChange = null)
                                    Text("All", modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
                                }
                            }
                            items(members) { member ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        val newIds = memberIds.toMutableSet()
                                        newIds.remove("0")
                                        val idStr = member.id.toString()
                                        if (newIds.contains(idStr)) newIds.remove(idStr) else newIds.add(idStr)
                                        memberIds = newIds
                                    }.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(checked = memberIds.contains(member.id.toString()), onCheckedChange = null)
                                    Text(member.name, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                DatePickerField(label = "Start Date", date = start) { start = it }
                Spacer(Modifier.height(8.dp))
                DatePickerField(label = "End Date", date = end) { end = it }

                Spacer(Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = { onApply(clubId, memberIds, start, end) },
                        enabled = start.isNotBlank() && end.isNotBlank()
                    ) { Text("Apply") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFilterDialog(
    clubs: List<DropdownItem>,
    members: List<MemberUiModel>,
    initialClubId: Int = 0,
    initialMemberId: Int = 0,
    onDismiss: () -> Unit,
    onClubSelected: (Int) -> Unit,
    onApply: (Int) -> Unit
) {
    var clubId by remember { mutableIntStateOf(initialClubId) }
    var selectedMemberId by remember { mutableIntStateOf(initialMemberId) }
    var isMemberDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onClubSelected(clubId)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Transaction Filters", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))

                AppDropdown(
                    label = "Select Club",
                    items = clubs,
                    selectedId = clubId,
                    onItemSelected = {
                        clubId = it.id
                        onClubSelected(it.id)
                    }
                )

                Spacer(Modifier.height(12.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick = { isMemberDropdownExpanded = !isMemberDropdownExpanded },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val selectedMemberName = members.find { it.id == selectedMemberId }?.name ?: if (selectedMemberId == 0) "All Members" else "Select Member"
                            Text(selectedMemberName, style = MaterialTheme.typography.bodyMedium)
                            Icon(
                                if (isMemberDropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (isMemberDropdownExpanded) {
                    Card(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).padding(top = 4.dp).border(0.5.dp, Color.LightGray.copy(alpha = 0.5f), MaterialTheme.shapes.small),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        LazyColumn {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        selectedMemberId = 0
                                        isMemberDropdownExpanded = false
                                    }.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(checked = selectedMemberId == 0, onCheckedChange = null)
                                    Text("All", modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold)
                                }
                            }
                            items(members) { member ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        selectedMemberId = member.id
                                        isMemberDropdownExpanded = false
                                    }.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(checked = selectedMemberId == member.id, onCheckedChange = null)
                                    Text(member.name, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = { onApply(selectedMemberId) }
                    ) { Text("Apply") }
                }
            }
        }
    }
}

@Composable
fun DatePickerField(label: String, date: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    OutlinedCard(
        onClick = {
            DatePickerDialog(context, { _, y, m, d ->
                val cal = Calendar.getInstance()
                cal.set(y, m, d)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                onDateSelected(sdf.format(cal.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Text(date, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ReportItem(report: ReportData) {
    val context = LocalContext.current
    val baseUrl = "http://192.168.18.72:7001/"
    val imagePath = report.userImage ?: ""
    val fullImageUrl = if (imagePath.isNotEmpty() && !imagePath.startsWith("http")) {
        baseUrl + imagePath.removePrefix("/")
    } else {
        imagePath
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Profile Picture (Round)
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onSecondary,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            var isError by remember(fullImageUrl) { mutableStateOf(false) }
                            
                            if (imagePath.isNotEmpty() && !isError) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(fullImageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = report.memberName,
                                    onState = { state ->
                                        isError = state is AsyncImagePainter.State.Error
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = (report.memberName ?: "U").take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = report.memberName?.replaceFirstChar { it.uppercase() } ?: "Unknown",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "ID: ${report.memberId ?: "N/A"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Club: ${report.clubName ?: "N/A"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                // Punch Type Chip
                val punchName = (report.punchName ?: "").lowercase()
                val isPunchIn = punchName.contains("in")
                val statusColor = if (isPunchIn) Color(0xFF10B981) else Color(0xFFEF4444)

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = (report.punchName ?: "N/A").uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.alpha(0.3f))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoColumn(
                    label = "Time",
                    value = report.checkTime ?: "N/A",
                    alignment = Alignment.Start
                )
                InfoColumn(
                    label = "Location",
                    value = report.location ?: "N/A",
                    alignment = Alignment.End
                )
            }
        }
    }
}

@Composable
fun InfoColumn(label: String, value: String, alignment: Alignment.Horizontal) {
    Column(horizontalAlignment = alignment) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TransactionItem(transaction: TransactionData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.name ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = transaction.planName ?: "No Plan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "₹${transaction.price ?: 0}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Black
                )
            }
            
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(modifier = Modifier.alpha(0.3f))
            Spacer(Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        "Start Date",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        transaction.startDate ?: "N/A",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Expiry Date",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        transaction.expiryDate ?: "N/A",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Validity: ${transaction.validity ?: "0"} ${transaction.validityType ?: ""}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportTabScreenPreview() {
    val mockReports = listOf(
        ReportData(
            checkTime = "10:00 AM",
            clubName = "Main Club",
            location = "Main Entrance",
            maskValue = 1,
            memberId = "M001",
            memberName = "John Doe",
            punchName = "Punch In",
            temperatureValue = "36.5",
            userImage = ""
        ),
        ReportData(
            checkTime = "06:00 PM",
            clubName = "Main Club",
            location = "Main Entrance",
            maskValue = 1,
            memberId = "M002",
            memberName = "Jane Smith",
            punchName = "Punch Out",
            temperatureValue = "36.7",
            userImage = ""
        )
    )
    
    val mockTransactions = listOf(
        TransactionData(
            name = "John Doe",
            planName = "Gold Membership",
            price = 5000,
            startDate = "01/01/2024",
            expiryDate = "31/12/2024",
            validity = "4",
            validityType = "Year"
        )
    )

    val mockState = ReportState(
        reports = mockReports,
        transactions = mockTransactions,
        totalAmount = 5000,
        totalCount = 1,
        startDate = "01/01/2024",
        endDate = "01/01/2024"
    )

    ClubManagementTheme {
        ReportTabContent(
            state = mockState,
            clubs = emptyList(),
            navController = rememberNavController(),
            onMenuClick = {},
            onLoadMembers = {},
            onGetReports = { _, _, _, _ -> },
            onGetTransactions = {}
        )
    }
}
