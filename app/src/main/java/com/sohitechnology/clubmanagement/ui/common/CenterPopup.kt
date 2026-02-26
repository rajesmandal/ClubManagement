package com.sohitechnology.clubmanagement.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sohitechnology.clubmanagement.ui.UiMessage
import com.sohitechnology.clubmanagement.ui.UiMessageType
import kotlinx.coroutines.delay

@Composable
fun CenterPopup(
    uiMessage: UiMessage,
    onDismiss: () -> Unit,
    autoDismissSeconds: Int = 0, // 0 means no auto-dismiss
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    var secondsLeft by remember { mutableIntStateOf(autoDismissSeconds) }

    // auto countdown only if autoDismissSeconds > 0
    LaunchedEffect(Unit) {
        if (autoDismissSeconds > 0) {
            while (secondsLeft > 0) {
                delay(1000) // 1 sec delay
                secondsLeft--
            }
            onDismiss() // auto dismiss
        }
    }

    Dialog(
        onDismissRequest = onDismiss, 
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {

        Card(
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = if (uiMessage.type == UiMessageType.ERROR)
                        Icons.Default.Error
                    else
                        Icons.Default.Info,
                    contentDescription = null,
                    tint = if (uiMessage.type == UiMessageType.ERROR)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = uiMessage.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = uiMessage.message,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        val cancelText = if (autoDismissSeconds > 0) "Cancel ($secondsLeft)" else "Cancel"
                        Text(cancelText)
                    }
                    
                    if (actionText != null && onAction != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(onClick = onAction) {
                            Text(actionText)
                        }
                    } else {
                        // If no specific action, clicking OK just dismisses
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(onClick = onDismiss) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}
