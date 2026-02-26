package com.sohitechnology.clubmanagement.ui.member

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sohitechnology.clubmanagement.R
import com.sohitechnology.clubmanagement.ui.theme.ClubManagementTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MemberItem(
    member: MemberUiModel,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    val baseUrl = "http://192.168.18.72:7001/"
    val imagePath = member.image ?: ""
    val fullImageUrl = if (imagePath.isNotEmpty() && !imagePath.startsWith("http")) {
        baseUrl + imagePath.removePrefix("/")
    } else {
        imagePath
    }

    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(0.5.dp, Color.Gray.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image Section with Surface/Shadow
                Surface(
                    modifier = Modifier
                        .size(60.dp)
                        .sharedElement(
                            rememberSharedContentState(key = "image-${member.memberId}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.onSecondary,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (fullImageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(fullImageUrl)
                                    .crossfade(true)
                                    .placeholder(R.drawable.logo)
                                    .error(R.drawable.logo)
                                    .build(),
                                contentDescription = member.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = (member.name ?: "U").take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                // Details Section
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = member.name.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .sharedElement(
                                        rememberSharedContentState(key = "name-${member.memberId}"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    ),
                                maxLines = 1
                            )
                            Text(
                                text = "@${member.userName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        // Status Chip
                        val status = (member.status ?: "Unknown").lowercase()
                        val statusColor = when (status) {
                            "active" -> Color(0xFF10B981)
                            "expired" -> Color(0xFFEF4444)
                            else -> Color(0xFF6B7280)
                        }

                        Surface(
                            color = statusColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(start = 8.dp)
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

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Member ID Badge
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ID: ${member.memberId ?: "N/A"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Club Name
                        Text(
                            text = member.clubName ?: "No Club Assigned",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun MemberItemPreview() {
    val member = MemberUiModel(
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
            AnimatedVisibility(visible = true, label = "MemberItemPreview") {
                MemberItem(
                    member = member,
                    onClick = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility
                )
            }
        }
    }
}
