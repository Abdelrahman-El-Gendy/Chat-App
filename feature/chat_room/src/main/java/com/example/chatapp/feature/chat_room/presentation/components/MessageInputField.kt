package com.example.chatapp.feature.chat_room.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chatapp.feature.chat_room.R

/**
 * Stateless message input field composable.
 * 
 * All state is hoisted to the parent (via ViewModel):
 * - text: Current input text
 * - selectedMediaUris: List of selected media URIs
 * - canSend: Whether the send button should be enabled
 * 
 * This composable is side-effect free - no LaunchedEffect, no internal state.
 * This makes it predictable, testable, and prevents unnecessary recompositions.
 * 
 * @param text The current input text (hoisted state)
 * @param onTextChange Callback when text changes
 * @param selectedMediaUris List of selected media URI strings (hoisted state)
 * @param canSend Whether the send button should be enabled (derived state)
 * @param onSendMessage Callback when send button is clicked
 * @param onPickMedia Callback to open media picker
 * @param onClearMedia Callback to clear selected media
 */
@Composable
fun MessageInputField(
    text: String,
    onTextChange: (String) -> Unit,
    selectedMediaUris: List<String>,
    canSend: Boolean,
    onSendMessage: () -> Unit,
    onPickMedia: () -> Unit,
    onClearMedia: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .navigationBarsPadding()
    ) {
        // Selected media preview with smooth expand/collapse animation
        AnimatedVisibility(
            visible = selectedMediaUris.isNotEmpty(),
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = 300f
                )
            ),
            exit = shrinkVertically(
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = 300f
                )
            )
        ) {
            MediaPreviewRow(
                selectedMediaUris = selectedMediaUris,
                onClearMedia = onClearMedia
            )
        }

        // Input field surface
        InputFieldSurface(
            text = text,
            onTextChange = onTextChange,
            hasSelectedMedia = selectedMediaUris.isNotEmpty(),
            canSend = canSend,
            onPickMedia = onPickMedia,
            onSend = onSendMessage
        )
    }
}

/**
 * Displays a horizontal row of selected media previews with a clear button.
 * Stateless composable - receives all data via parameters.
 */
@Composable
private fun MediaPreviewRow(
    selectedMediaUris: List<String>,
    onClearMedia: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            selectedMediaUris.forEach { uri ->
                MediaThumbnail(uri = uri)
            }
            ClearMediaButton(onClick = onClearMedia)
        }
    }
}

/**
 * Single media thumbnail preview.
 */
@Composable
private fun MediaThumbnail(uri: String) {
    Box(modifier = Modifier.size(70.dp)) {
        AsyncImage(
            model = uri,
            contentDescription = "Selected media thumbnail",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Button to clear all selected media.
 */
@Composable
private fun ClearMediaButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    
    IconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
            .size(48.dp)
            .semantics {
                contentDescription = "Clear all selected media"
            }
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * The main input surface containing text field, media button, and send button.
 * Stateless composable - canSend is passed in as a parameter (derived in ViewModel).
 */
@Composable
private fun InputFieldSurface(
    text: String,
    onTextChange: (String) -> Unit,
    hasSelectedMedia: Boolean,
    canSend: Boolean,
    onPickMedia: () -> Unit,
    onSend: () -> Unit
) {
    // Compute shape only when hasSelectedMedia changes
    val surfaceShape = remember(hasSelectedMedia) {
        if (hasSelectedMedia)
            RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        else RoundedCornerShape(24.dp)
    }
    
    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        shape = surfaceShape,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Add media button
                AddMediaButton(onClick = onPickMedia)

                // Text input
                MessageTextField(
                    text = text,
                    onTextChange = onTextChange,
                    modifier = Modifier.weight(1f)
                )

                // Send button with visual feedback
                SendButton(
                    canSend = canSend,
                    onClick = onSend
                )
            }
            
            // Character counter when approaching limit
            CharacterCounter(text = text)
        }
    }
}

/**
 * Button to add media attachments.
 */
@Composable
private fun AddMediaButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    
    IconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .size(48.dp)
            .semantics {
                contentDescription = "Add media attachment"
            }
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Text input field for message content.
 */
@Composable
private fun MessageTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier,
        placeholder = {
            Text(
                stringResource(R.string.type_a_message),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        maxLines = 6,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

/**
 * Send message button with enabled/disabled state and visual feedback.
 */
@Composable
private fun SendButton(
    canSend: Boolean,
    onClick: () -> Unit
) {
    // Opacity: full when enabled, 30% when disabled
    val opacity by animateFloatAsState(
        targetValue = if (canSend) 1.0f else 0.3f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = 300f
        ),
        label = "send_button_opacity"
    )
    
    val interactionSource = remember { MutableInteractionSource() }
    
    IconButton(
        onClick = onClick,
        enabled = canSend,
        interactionSource = interactionSource,
        modifier = Modifier
            .padding(4.dp)
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = opacity.coerceIn(0f, 1f)),
                shape = CircleShape
            )
            .size(48.dp)
            .semantics {
                contentDescription = if (canSend) "Send message" else "Send button disabled"
            }
    ) {
        Icon(
            Icons.Default.Send,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Character counter that shows when approaching the character limit.
 * Shows at 80% of the limit (default 1000 characters).
 */
@Composable
private fun CharacterCounter(text: String) {
    val maxCharacters = 1000
    val characterCount = text.length
    val isApproachingLimit = characterCount >= maxCharacters * 0.8f
    
    AnimatedVisibility(
        visible = isApproachingLimit,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 300)
        ) + expandVertically(
            animationSpec = tween(durationMillis = 300)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 300)
        ) + shrinkVertically(
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        val remaining = maxCharacters - characterCount
        
        Text(
            text = "$characterCount / $maxCharacters",
            style = MaterialTheme.typography.labelSmall,
            color = if (remaining < 0) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Character count: $characterCount out of $maxCharacters"
                }
        )
    }
}
