package com.example.chatapp.feature.chat_room.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.chatapp.core.ui.component.UserAvatar
import com.example.chatapp.feature.chat_room.R
import com.example.chatapp.feature.chat_room.presentation.components.MessageInputField
import com.example.chatapp.feature.chat_room.presentation.components.MessageItem
import com.example.chatapp.feature.chat_room.presentation.model.ChatEffect
import com.example.chatapp.feature.chat_room.presentation.model.ChatIntent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val selectedMedia = remember { mutableStateListOf<Uri>() }
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ChatEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ChatEffect.ScrollToBottom -> {
                    if (state.messages.isNotEmpty()) {
                        listState.animateScrollToItem(state.messages.size - 1)
                    }
                }
                is ChatEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ChatEffect.NavigateBack -> {
                    onBack()
                }
            }
        }
    }

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            selectedMedia.clear()
            selectedMedia.addAll(uris)
        }
    )

    // Pagination trigger
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (index == 0 && state.messages.isNotEmpty() && !state.isPaginatedLoading) {
                    viewModel.onIntent(ChatIntent.LoadMoreMessages)
                }
            }
    }

    // Scroll to bottom on new messages
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        UserAvatar(username = state.currentUserName, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = state.currentUserName.ifEmpty { stringResource(R.string.chat_title) },
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.online),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                if (state.typingUsers.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.typing_indicator, state.typingUsers.joinToString(", ")),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = 16.dp, bottom = 4.dp)
                            .animateContentSize()
                    )
                }
                MessageInputField(
                    onSendMessage = { text, uris ->
                        viewModel.onIntent(ChatIntent.SendMessage(text, uris))
                    },
                    onPickMedia = {
                        mediaPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    },
                    selectedMedia = selectedMedia,
                    onClearMedia = { selectedMedia.clear() },
                    onTyping = { isTyping ->
                        viewModel.onIntent(ChatIntent.SetTyping(isTyping))
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
            ) {
                if (state.isPaginatedLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }

                items(state.messages, key = { it.id }) { message ->
                    MessageItem(
                        message = message,
                        isOwnMessage = message.senderId == state.currentUserId,
                        onDelete = { messageId ->
                            viewModel.onIntent(ChatIntent.DeleteMessage(messageId))
                        },
                        onRetry = { msg ->
                            viewModel.onIntent(ChatIntent.RetryMessage(msg))
                        }
                    )
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
