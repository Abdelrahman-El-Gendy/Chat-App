package com.example.chatapp.feature.chat_room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.model.MessageStatus
import com.example.chatapp.core.ui.component.UserAvatar
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MessageItem(
    message: Message,
    isOwnMessage: Boolean,
    onDelete: (String) -> Unit,
    onRetry: (Message) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isOwnMessage) {
            UserAvatar(username = message.senderName)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start) {
            Box {
                Surface(
                    tonalElevation = if (isOwnMessage) 2.dp else 0.dp,
                    shadowElevation = 2.dp,
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isOwnMessage) 20.dp else 4.dp,
                        bottomEnd = if (isOwnMessage) 4.dp else 20.dp
                    ),
                    color = if (isOwnMessage) MaterialTheme.colorScheme.primaryContainer 
                            else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { if (isOwnMessage) showMenu = true }
                        )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (!isOwnMessage) {
                            Text(
                                text = message.senderName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        message.text?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isOwnMessage) MaterialTheme.colorScheme.onPrimaryContainer 
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        val mediaUrls = message.mediaUrls
                        if (!mediaUrls.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(mediaUrls) { url ->
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(160.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.align(Alignment.End),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOwnMessage) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                        else Color.Gray,
                                fontSize = 10.sp
                            )
                            if (isOwnMessage) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (message.status) {
                                        MessageStatus.SENDING -> "..."
                                        MessageStatus.SENT -> "✓"
                                        MessageStatus.FAILED -> "⚠"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (message.status == MessageStatus.FAILED) Color.Red 
                                            else if (isOwnMessage) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                            else Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            onDelete(message.id)
                            showMenu = false
                        }
                    )
                }
            }
            
            if (isOwnMessage && message.status == MessageStatus.FAILED) {
                TextButton(
                    onClick = { onRetry(message) },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Retry", color = Color.Red, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
