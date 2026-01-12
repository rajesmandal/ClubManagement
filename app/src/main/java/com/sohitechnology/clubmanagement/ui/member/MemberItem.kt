package com.sohitechnology.clubmanagement.ui.member

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MemberItem(
    member: MemberUiModel
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = member.name.take(1).uppercase())
            }
            Spacer(Modifier.width(12.dp))

            // Member Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Status Badge
                    val statusColor = when (member.status.lowercase()) {
                        "active" -> Color(0xFF2E7D32) // Green
                        "expired" -> Color(0xFFD32F2F) // Red
                        else -> Color(0xFF757575) // Gray for deActivated/Other
                    }

                    Text(
                        text = member.status.uppercase(),
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center, // Text ko center karne ke liye
                        modifier = Modifier
                            .widthIn(min = 80.dp) // <--- Sabka size same rakhne ke liye Minimum Width
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(statusColor.copy(alpha = 0.1f))
                            .border(0.5.dp, statusColor.copy(alpha = 0.3f), MaterialTheme.shapes.extraSmall)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = member.userName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = member.memberId,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = member.clubName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemberItemPreview() {
//1. Ek dummy data object banayein
    val dummyMember = MemberUiModel(
        memberId = "1",
        name = "Rahul Sharma",
        userName = "rahul",
        image = "Admin",
        status = "active",
        clubName = "Club Name"
    )

    // 2. Apne actual Composable ko call karein aur data pass karein
    MaterialTheme { // Theme wrap karna acchi practice hai
        MemberItem(member = dummyMember)
    }}