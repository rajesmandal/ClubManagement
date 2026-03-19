package com.sohitechnology.gymstudio.hammer.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sohitechnology.gymstudio.hammer.data.model.MemberCountData
import com.sohitechnology.gymstudio.hammer.data.model.MemberDetailData
import com.sohitechnology.gymstudio.hammer.ui.member.report.GymUsageStats
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun MemberDashboardCard(data: MemberDetailData, onReload: () -> Unit, isLoading: Boolean) {
    val premiumGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1A1A1A),
            Color(0xFF2D2D2D),
            Color(0xFF1A1A1A)
        )
    )

    // Use transaction dates if available as requested by user
    val activeTransaction = data.getMemberTransaction?.firstOrNull()
    val startDate = activeTransaction?.startDate ?: data.startDate
    val expiryDate = activeTransaction?.expiryDate ?: data.expiryDate

    val progress = remember(startDate, expiryDate) {
        calculateProgress(startDate, expiryDate)
    }

    val daysLeft = remember(expiryDate) {
        calculateDaysLeft(expiryDate)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(premiumGradient)
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PREMIUM MEMBERSHIP",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFFD700).copy(alpha = 0.8f),
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = data.name?.uppercase() ?: "USER",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    IconButton(
                        onClick = onReload,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f)),
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DashboardItemPremium("MEMBER ID", data.memberId ?: "N/A")
                    DashboardItemPremium("STATUS", data.status?.uppercase() ?: "N/A")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Membership Progress",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = if (daysLeft > 0) "$daysLeft Days Left" else "Expired",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (daysLeft > 5) Color(0xFFFFD700) else Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFFD700),
                        trackColor = Color.White.copy(alpha = 0.1f),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = startDate ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Text(
                            text = expiryDate ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyInsightsCard(stats: GymUsageStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Monthly Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            InsightItem(
                icon = Icons.Default.LocalFireDepartment,
                iconColor = Color(0xFFF97316),
                title = "Days Streak",
                value = "${stats.currentStreak} Days",
                description = "Consistent workouts"
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(modifier = Modifier.alpha(0.1f))
            Spacer(modifier = Modifier.height(20.dp))

            InsightItem(
                icon = Icons.Default.CalendarMonth,
                iconColor = Color(0xFF3B82F6),
                title = "Best Day",
                value = stats.bestDay,
                description = "Most punches recorded"
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(modifier = Modifier.alpha(0.1f))
            Spacer(modifier = Modifier.height(20.dp))

            InsightItem(
                icon = Icons.Default.Poll, // Three bar icon
                iconColor = Color(0xFF10B981),
                title = "Consistency",
                value = "${stats.consistency}%",
                description = "Based on monthly activity",
                showProgressBar = true
            )
        }
    }
}

@Composable
fun MemberCalendarCard(activeDates: Set<String>) {
    val calendar = remember { Calendar.getInstance() }
    val currentMonth = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time) }
    val daysInMonth = remember { calendar.getActualMaximum(Calendar.DAY_OF_MONTH) }

    val dayNames = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    val dateSdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = currentMonth,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Day Names Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                dayNames.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (day == "SUN") Color.Gray else Color(0xFF10B981)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dates Grid
            val firstDayOfWeek = remember {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.get(Calendar.DAY_OF_WEEK) - 1
            }

            val totalCells = (daysInMonth + firstDayOfWeek + 6) / 7 * 7

            Column {
                for (row in 0 until totalCells / 7) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val dateNum = cellIndex - firstDayOfWeek + 1

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (dateNum in 1..daysInMonth) {
                                    val dateStr = remember(dateNum) {
                                        val cal = Calendar.getInstance()
                                        cal.set(Calendar.DAY_OF_MONTH, dateNum)
                                        dateSdf.format(cal.time)
                                    }
                                    val hasPunch = activeDates.contains(dateStr)

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (hasPunch) Color(0xFF10B981) else Color.Gray.copy(alpha = 0.2f))
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
fun InsightItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    description: String,
    showProgressBar: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = iconColor.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Text(text = description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            if (showProgressBar) {
                Spacer(modifier = Modifier.height(8.dp))
                val progress = try { value.replace("%", "").toFloat() / 100f } catch (e: Exception) { 0f }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = iconColor,
                    trackColor = iconColor.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun UsageDonutChart(stats: GymUsageStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Performance Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Monthly Performance
                UsageDonutIndicator(
                    value = stats.averageMinutesPerDay,
                    label = "MONTHLY",
                    subLabel = "Avg Min/Day",
                    color = Color(0xFF10B981)
                )

                // Weekly Performance
                UsageDonutIndicator(
                    value = stats.weekAverageMinutesPerDay,
                    label = "WEEKLY",
                    subLabel = "Avg Min/Day",
                    color = Color(0xFF3B82F6)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(modifier = Modifier.alpha(0.3f))
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UsageStatItemSmall("Month Trips", stats.gymVisitCount.toString(), Color(0xFF10B981))
                UsageStatItemSmall("Week Trips", stats.weekVisitCount.toString(), Color(0xFF3B82F6))
                UsageStatItemSmall("Month Time", "${stats.totalMinutes / 60}h", Color(0xFF8B5CF6))
            }
        }
    }
}

@Composable
fun UsageDonutIndicator(value: Long, label: String, subLabel: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
            val maxMins = 120f
            val sweepAngle = (value.toFloat() / maxMins * 360f).coerceAtMost(360f)
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 10.dp.toPx()
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.1f),
                    style = Stroke(width = strokeWidth)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "MINS",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color)
        Text(text = subLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
    }
}

@Composable
fun UsageStatItemSmall(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
    }
}

@Composable
fun UsageStatItem(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DashboardItemPremium(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun DashboardItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun PlanDetailsCard(transaction: com.sohitechnology.gymstudio.hammer.data.model.MemberTransaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = transaction.planName ?: "Plan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₹${transaction.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(modifier = Modifier.alpha(0.3f))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Start Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    Text(transaction.startDate ?: "N/A", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Expiry Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    Text(transaction.expiryDate ?: "N/A", style = MaterialTheme.typography.bodyMedium)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Validity: ${transaction.validity} ${transaction.validityType}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MemberCountCard(data: MemberCountData, onReload: () -> Unit, isLoading: Boolean) {
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
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Member Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
                Surface(onClick = onReload,modifier = Modifier.align(Alignment.CenterEnd).size(36.dp), shape = RoundedCornerShape(50.dp), color = MaterialTheme.colorScheme.background,  enabled = !isLoading) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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

private fun parseDate(dateStr: String?): Long? {
    if (dateStr == null) return null
    val formats = listOf("dd-MM-yyyy", "dd/MM/yyyy", "yyyy-MM-dd", "yyyy/MM/dd")
    for (format in formats) {
        try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.isLenient = false
            return sdf.parse(dateStr)?.time
        } catch (e: Exception) {
            continue
        }
    }
    return null
}

private fun calculateProgress(startDate: String?, expiryDate: String?): Float {
    val start = parseDate(startDate) ?: return 0f
    val expiry = parseDate(expiryDate) ?: return 0f
    val current = System.currentTimeMillis()

    if (current >= expiry) return 1f
    if (current <= start) return 0f

    val total = expiry - start
    val elapsed = current - start
    
    return if (total > 0) {
        (elapsed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
}

private fun calculateDaysLeft(expiryDate: String?): Long {
    val expiry = parseDate(expiryDate) ?: return 0
    val current = System.currentTimeMillis()

    if (current >= expiry) return 0

    val diff = expiry - current
    return TimeUnit.MILLISECONDS.toDays(diff)
}
