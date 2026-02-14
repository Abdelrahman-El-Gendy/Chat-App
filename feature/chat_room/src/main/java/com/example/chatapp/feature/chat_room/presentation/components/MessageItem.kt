package com.example.chatapp.feature.chat_room.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.model.MessageStatus
import com.example.chatapp.core.ui.component.UserAvatar
import com.example.chatapp.feature.chat_room.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Message item composable - displays a single chat message.
 * 
 * The showMenu state is local (UI-only) since it doesn't need to survive configuration changes.
 * All other state is hoisted to prevent unnecessary recompositions.
 * 
 * @param message The message data to display
 * @param isOwnMessage Whether this message belongs to the current user
 * @param onDelete Callback when delete action is triggered
 * @param onRetry Callback when retry action is triggered (for failed messages)
 */
@Composable
fun MessageItem(
    message: Message,
    isOwnMessage: Boolean,
    onDelete: (String) -> Unit,
    onRetry: (Message) -> Unit
) {
    // Local UI-only state for dropdown menu
    var showMenu by remember { mutableStateOf(false) }
    
    // Precompute alignment to avoid recalculation during recomposition
    val horizontalArrangement = remember(isOwnMessage) {
        if (isOwnMessage) Arrangement.End else Arrangement.Start
    }
    val columnAlignment = remember(isOwnMessage) {
        if (isOwnMessage) Alignment.End else Alignment.Start
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Bottom
    ) {
        // Avatar for other users
        if (!isOwnMessage) {
            UserAvatar(username = message.senderName)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(horizontalAlignment = columnAlignment) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 300
                    )
                ) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(
                        durationMillis = 300
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = 300
                    )
                ) + scaleOut(
                    targetScale = 0.9f,
                    animationSpec = tween(
                        durationMillis = 300
                    )
                )
            ) {
                Box {
                    MessageBubble(
                        message = message,
                        isOwnMessage = isOwnMessage,
                        onLongClick = { if (isOwnMessage) showMenu = true }
                    )

                    // Context menu
                    MessageContextMenu(
                        expanded = showMenu,
                        onDismiss = { showMenu = false },
                        onDelete = { onDelete(message.id) }
                    )
                }
            }

            // Retry button for failed messages
            if (isOwnMessage && message.status == MessageStatus.FAILED) {
                RetryButton(onClick = { onRetry(message) })
            }
        }
    }
}

/**
 * Context menu for message actions (delete).
 */
@Composable
private fun MessageContextMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .width(160.dp)
            .padding(4.dp)
            .semantics {
                contentDescription = "Message options menu"
            }
    ) {
        DropdownMenuItem(
            text = { 
                Text(
                    stringResource(R.string.delete),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            onClick = {
                onDelete()
                onDismiss()
            },
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Delete message"
                }
        )
    }
}

/**
 * Retry button for failed messages.
 */
@Composable
private fun RetryButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource,
        modifier = Modifier
            .height(48.dp)
            .widthIn(min = 48.dp)
            .semantics {
                contentDescription = "Retry sending message"
            }
    ) {
        Text(
            stringResource(R.string.retry),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageBubble(
    message: Message,
    isOwnMessage: Boolean,
    onLongClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    val senderInfo = if (isOwnMessage) "Your message" else "Message from ${message.senderName}"
    
    Surface(
        tonalElevation = if (isOwnMessage) 2.dp else 1.dp,
        shadowElevation = if (isOwnMessage) 2.dp else 1.dp,
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
                onLongClick = onLongClick,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = true,
                    radius = 140.dp,
                    color = if (isOwnMessage) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            .semantics {
                contentDescription = senderInfo
            }
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize()
        ) {
            // Sender name (for other users)
            if (!isOwnMessage) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Message text
            message.text?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isOwnMessage) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Media content
            val mediaUrls = message.mediaUrls
            if (!mediaUrls.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                MediaGallery(mediaUrls = mediaUrls)
            }

            // Timestamp and status
            Spacer(modifier = Modifier.height(4.dp))
            MessageFooter(
                message = message,
                isOwnMessage = isOwnMessage
            )
        }
    }
}

@Composable
private fun MediaGallery(mediaUrls: List<String>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(mediaUrls) { url ->
            AsyncImage(
                model = url,
                contentDescription = "Attached media image",
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * Message footer showing timestamp and status.
 * Uses remembered date formatter to avoid creating new instance on every recomposition.
 */
@Composable
private fun MessageFooter(
    message: Message,
    isOwnMessage: Boolean
) {
    // Remember the date formatter to avoid recreation on each recomposition
    // Using 12-hour format with AM/PM
    val dateFormatter = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    }
    
    // Compute formatted time only when timestamp changes
    val formattedTime = remember(message.timestamp) {
        dateFormatter.format(Date(message.timestamp))
    }
    
    // Precompute colors to avoid recalculation
    val timestampColor = if (isOwnMessage) {
        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timestamp
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.labelSmall,
            color = timestampColor
        )

        // Status indicator (for own messages)
        if (isOwnMessage) {
            Spacer(modifier = Modifier.width(4.dp))
            MessageStatusIndicator(
                status = message.status,
                defaultColor = timestampColor
            )
        }
    }
}

/**
 * Status indicator icon for message delivery status.
 */
@Composable
private fun MessageStatusIndicator(
    status: MessageStatus,
    defaultColor: Color
) {
    val (statusText, statusColor, contentDesc) = remember(status) {
        when (status) {
            MessageStatus.SENDING -> Triple("...", defaultColor, "Message sending")
            MessageStatus.SENT -> Triple("✓", defaultColor, "Message sent")
            MessageStatus.FAILED -> Triple("⚠", Color.Red, "Message failed to send")
        }
    }
    
    // Animate color changes
    val animatedColor by animateColorAsState(
        targetValue = statusColor,
        animationSpec = tween(durationMillis = 300)
    )
    
    // Animate scale for sending status
    val scale by animateFloatAsState(
        targetValue = if (status == MessageStatus.SENDING) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        )
    )
    
    Text(
        text = statusText,
        style = MaterialTheme.typography.labelSmall,
        color = animatedColor,
        modifier = Modifier
            .scale(scale)
            .semantics {
                contentDescription = contentDesc
            }
    )
}
