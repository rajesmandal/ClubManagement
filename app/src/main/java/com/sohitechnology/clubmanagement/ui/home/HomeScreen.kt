package com.sohitechnology.clubmanagement.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.sohitechnology.clubmanagement.data.model.MemberCountData
import com.sohitechnology.clubmanagement.navigation.AppBottomBar
import com.sohitechnology.clubmanagement.navigation.MainRoute
import com.sohitechnology.clubmanagement.ui.common.AppTopBar
import com.sohitechnology.clubmanagement.ui.common.EmptyState
import com.sohitechnology.clubmanagement.ui.member.MemberItem
import com.sohitechnology.clubmanagement.ui.member.MemberUiModel
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onMenuClick: () -> Unit = {},
    onMemberClick: (MemberUiModel) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val memberCount by viewModel.memberCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val expiryMembers by viewModel.expiryMembers.collectAsState()
    val isExpiryLoading by viewModel.isExpiryLoading.collectAsState()

    // Permission Request Logic
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        var hasNotificationPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                hasNotificationPermission = isGranted
            }
        )

        LaunchedEffect(Unit) {
            if (!hasNotificationPermission) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getMemberCount()
        viewModel.getMemberExpiry()
    }

    HomeContent(
        memberCount = memberCount,
        isLoading = isLoading,
        error = error,
        expiryMembers = expiryMembers,
        isExpiryLoading = isExpiryLoading,
        navController = navController,
        onMenuClick = onMenuClick,
        onMemberClick = onMemberClick,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeContent(
    memberCount: MemberCountData?,
    isLoading: Boolean,
    error: String?,
    expiryMembers: List<MemberUiModel>,
    isExpiryLoading: Boolean,
    navController: NavHostController,
    onMenuClick: () -> Unit,
    onMemberClick: (MemberUiModel) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Home",
                onMenuClick = onMenuClick,
                onNotificationClick = {
                    navController.navigate(MainRoute.Notification.route)
                }
            )
        },
        bottomBar = {
            AppBottomBar(navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && memberCount == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null && memberCount == null) {
                Text(
                    text = error ?: "Unknown Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    memberCount?.let { data ->
                        MemberCountCard(data)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Upcoming Expiries",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                    )
                    
                    if (isExpiryLoading && expiryMembers.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(30.dp))
                        }
                    } else if (expiryMembers.isEmpty()) {
                        EmptyState(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            title = "No Expiries Soon",
                            description = "No members are expiring in the near future."
                        )
                    } else {
                        expiryMembers.forEach { member ->
                            MemberItem(
                                member = member,
                                onClick = { onMemberClick(member) },
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

@Composable
fun MemberCountCard(data: MemberCountData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(0.5.dp, Color.Gray.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Member Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
                    DonutChart(
                        data = data,
                        modifier = Modifier.fillMaxSize()
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (data.all ?: 0).toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "TOTAL",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    LegendItem(
                        color = Color(0xFF10B981), 
                        label = "Active", 
                        count = data.active ?: 0,
                        gradient = Brush.linearGradient(listOf(Color(0xFF34D399), Color(0xFF10B981)))
                    )
                    LegendItem(
                        color = Color(0xFF6B7280), 
                        label = "Deactive", 
                        count = data.deactive ?: 0,
                        gradient = Brush.linearGradient(listOf(Color(0xFF9CA3AF), Color(0xFF6B7280)))
                    )
                    LegendItem(
                        color = Color(0xFFEF4444), 
                        label = "Expired", 
                        count = data.expired ?: 0,
                        gradient = Brush.linearGradient(listOf(Color(0xFFF87171), Color(0xFFEF4444)))
                    )
                    LegendItem(
                        color = Color(0xFF3B82F6), 
                        label = "Today Renew", 
                        count = data.todayRenew ?: 0,
                        gradient = Brush.linearGradient(listOf(Color(0xFF60A5FA), Color(0xFF3B82F6)))
                    )
                }
            }
        }
    }
}

@Composable
fun DonutChart(data: MemberCountData, modifier: Modifier = Modifier) {
    val total = (data.all ?: 0).toFloat()
    
    val activeAngle = if (total > 0) ((data.active ?: 0) / total) * 360f else 0f
    val deactiveAngle = if (total > 0) ((data.deactive ?: 0) / total) * 360f else 0f
    val expiredAngle = if (total > 0) ((data.expired ?: 0) / total) * 360f else 0f
    val todayRenewAngle = if (total > 0) ((data.todayRenew ?: 0) / total) * 360f else 0f

    Canvas(modifier = modifier) {
        val strokeWidth = 14.dp.toPx()
        val trackColor = Color.Gray.copy(alpha = 0.1f)
        
        // Background Track
        drawCircle(
            color = trackColor,
            style = Stroke(width = strokeWidth),
            radius = size.minDimension / 2 - strokeWidth / 2
        )

        var currentStartAngle = -90f
        
        // Active Arc
        if (activeAngle > 0) {
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to Color(0xFF34D399),
                    1.0f to Color(0xFF10B981)
                ),
                startAngle = currentStartAngle,
                sweepAngle = activeAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            currentStartAngle += activeAngle
        }
        
        // Deactive Arc
        if (deactiveAngle > 0) {
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to Color(0xFF9CA3AF),
                    1.0f to Color(0xFF6B7280)
                ),
                startAngle = currentStartAngle,
                sweepAngle = deactiveAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            currentStartAngle += deactiveAngle
        }
        
        // Expired Arc
        if (expiredAngle > 0) {
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to Color(0xFFF87171),
                    1.0f to Color(0xFFEF4444)
                ),
                startAngle = currentStartAngle,
                sweepAngle = expiredAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            currentStartAngle += expiredAngle
        }

        // Today Renew Arc
        if (todayRenewAngle > 0) {
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to Color(0xFF60A5FA),
                    1.0f to Color(0xFF3B82F6)
                ),
                startAngle = currentStartAngle,
                sweepAngle = todayRenewAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, count: Int, gradient: Brush) {
    Surface(
        color = color.copy(alpha = 0.05f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(gradient, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val mockMemberCount = MemberCountData(
        active = 50,
        deactive = 10,
        expired = 5,
        all = 65,
        todayRenew = 2
    )

    val mockExpiryMembers = listOf(
        MemberUiModel(
            id = 1,
            memberId = "M001",
            name = "John Doe",
            userName = "johndoe",
            password = "",
            image = "",
            status = "Active",
            gender = "Male",
            contactNo = "1234567890",
            emailId = "john@example.com",
            clubName = "Main Club",
            clubId = 1,
            birthDay = "01-01-1990",
            hireDay = "01-01-2023",
            address = "123 Street",
            nationality = "Indian",
            startDate = "01-01-2024",
            expiryDate = "01-02-2024"
        ),
        MemberUiModel(
            id = 2,
            memberId = "M002",
            name = "Jane Smith",
            userName = "janesmith",
            password = "",
            image = "",
            status = "Expired",
            gender = "Female",
            contactNo = "0987654321",
            emailId = "jane@example.com",
            clubName = "Secondary Club",
            clubId = 2,
            birthDay = "05-05-1995",
            hireDay = "15-05-2022",
            address = "456 Avenue",
            nationality = "Indian",
            startDate = "01-01-2023",
            expiryDate = "01-01-2024"
        )
    )

    ClubManagementTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true, label = "HomeScreenPreview") {
                HomeContent(
                    memberCount = mockMemberCount,
                    isLoading = false,
                    error = null,
                    expiryMembers = mockExpiryMembers,
                    isExpiryLoading = false,
                    navController = rememberNavController(),
                    onMenuClick = {},
                    onMemberClick = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility
                )
            }
        }
    }
}
