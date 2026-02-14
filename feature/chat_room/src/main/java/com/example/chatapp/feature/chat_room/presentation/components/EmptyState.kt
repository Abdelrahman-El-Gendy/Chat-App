package com.example.chatapp.feature.chat_room.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Empty state composable - displays when no messages are available.
 * Shows a friendly icon, message, and optional action button.
 * 
 * @param icon The icon to display
 * @param title The title text
 * @param description The description text
 * @param actionLabel Optional label for action button
 * @param onAction Optional callback for action button
 * @param modifier Modifier for the composable
 */
@Composable
fun EmptyState(
    icon: ImageVector = Icons.Default.Info,
    title: String = "No Messages",
    description: String = "Start a conversation by sending a message",
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .animateContentSize(
                animationSpec = tween(durationMillis = 300)
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 24.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Description
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Action button
                if (actionLabel != null && onAction != null) {
                    Button(
                        onClick = onAction,
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(min = 120.dp)
                    ) {
                        Text(actionLabel)
                    }
                }
            }
        }
    }
}
