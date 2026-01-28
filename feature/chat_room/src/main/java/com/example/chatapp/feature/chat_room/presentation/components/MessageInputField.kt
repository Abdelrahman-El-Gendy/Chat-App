package com.example.chatapp.feature.chat_room.presentation.components

import android.net.Uri
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chatapp.feature.chat_room.R

@Composable
fun MessageInputField(
    onSendMessage: (String?, List<String>?) -> Unit,
    onPickMedia: () -> Unit,
    selectedMedia: List<Uri> = emptyList(),
    onClearMedia: () -> Unit = {},
    onTyping: (Boolean) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }

    // Typing status logic
    LaunchedEffect(text) {
        if (text.isNotBlank()) {
            onTyping(true)
            kotlinx.coroutines.delay(3000)
            onTyping(false)
        } else {
            onTyping(false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .navigationBarsPadding()
    ) {
        // Selected media preview
        if (selectedMedia.isNotEmpty()) {
            MediaPreviewRow(
                selectedMedia = selectedMedia,
                onClearMedia = onClearMedia
            )
        }

        // Input field surface
        InputFieldSurface(
            text = text,
            onTextChange = { text = it },
            hasSelectedMedia = selectedMedia.isNotEmpty(),
            onPickMedia = onPickMedia,
            onSend = {
                if (text.isNotBlank() || selectedMedia.isNotEmpty()) {
                    onSendMessage(text, selectedMedia.map { it.toString() })
                    text = ""
                    onClearMedia()
                }
            }
        )
    }
}

@Composable
private fun MediaPreviewRow(
    selectedMedia: List<Uri>,
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
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            selectedMedia.forEach { uri ->
                Box(modifier = Modifier.size(70.dp)) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            IconButton(
                onClick = onClearMedia,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                    .size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.clear_all),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun InputFieldSurface(
    text: String,
    onTextChange: (String) -> Unit,
    hasSelectedMedia: Boolean,
    onPickMedia: () -> Unit,
    onSend: () -> Unit
) {
    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        shape = if (hasSelectedMedia)
            RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        else RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Add media button
            IconButton(onClick = onPickMedia) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_media),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Text input
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
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

            // Send button
            val canSend = text.isNotBlank() || hasSelectedMedia
            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        if (canSend) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        CircleShape
                    )
                    .size(40.dp)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = stringResource(R.string.send),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
