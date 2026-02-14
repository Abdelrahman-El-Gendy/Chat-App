package com.example.chatapp.feature.chat_room.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.ui.component.UserAvatar
import com.example.chatapp.feature.chat_room.R
import com.example.chatapp.feature.chat_room.presentation.components.EmptyState
import com.example.chatapp.feature.chat_room.presentation.components.MessageInputField
import com.example.chatapp.feature.chat_room.presentation.components.MessageItem
import com.example.chatapp.feature.chat_room.presentation.model.ChatEffect
import com.example.chatapp.feature.chat_room.presentation.model.ChatIntent
import com.example.chatapp.feature.chat_room.presentation.model.ChatState
import kotlinx.coroutines.flow.collectLatest

/**
 * ChatScreen - Container composable that handles side effects and delegates to stateless content.
 * 
 * This follows the state hoisting pattern where:
 * - State is collected from ViewModel
 * - Side effects (navigation, snackbar) are handled via LaunchedEffect
 * - All rendering is delegated to stateless ChatScreenContent
 */
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Side effect handler - isolated from composition
    ChatScreenEffectHandler(
        effectFlow = viewModel.effect,
        snackbarHostState = snackbarHostState,
        listState = listState,
        messagesSize = state.messages.size,
        onNavigateBack = onBack
    )

    // Pagination effect - triggers when scrolled to top
    PaginationEffect(
        listState = listState,
        hasMessages = state.messages.isNotEmpty(),
        isPaginatedLoading = state.isPaginatedLoading,
        onLoadMore = { viewModel.onIntent(ChatIntent.LoadMoreMessages) }
    )

    // Scroll to bottom effect - when new messages arrive
    ScrollToBottomEffect(
        listState = listState,
        messagesSize = state.messages.size
    )

    // Media picker launcher - result callback sends intent to ViewModel
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            viewModel.onIntent(ChatIntent.UpdateSelectedMedia(uris.map { it.toString() }))
        }
    )

    // Stateless content - pure UI rendering
    ChatScreenContent(
        state = state,
        listState = listState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onMessageInputChange = { text ->
            viewModel.onIntent(ChatIntent.UpdateMessageInput(text))
        },
        onSendMessage = { viewModel.onIntent(ChatIntent.SendMessage) },
        onPickMedia = {
            mediaPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        },
        onClearMedia = { viewModel.onIntent(ChatIntent.ClearSelectedMedia) },
        onDeleteMessage = { messageId ->
            viewModel.onIntent(ChatIntent.DeleteMessage(messageId))
        },
        onRetryMessage = { message ->
            viewModel.onIntent(ChatIntent.RetryMessage(message))
        }
    )
}

/**
 * Handles side effects from the ViewModel's effect channel.
 * Isolated to prevent triggering unnecessary recompositions.
 */
@Composable
private fun ChatScreenEffectHandler(
    effectFlow: kotlinx.coroutines.flow.Flow<ChatEffect>,
    snackbarHostState: SnackbarHostState,
    listState: LazyListState,
    messagesSize: Int,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is ChatEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is ChatEffect.ScrollToBottom -> {
                    if (messagesSize > 0) {
                        listState.animateScrollToItem(messagesSize - 1)
                    }
                }
                is ChatEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
                is ChatEffect.NavigateBack -> onNavigateBack()
            }
        }
    }
}

/**
 * Handles pagination when user scrolls to the top of the list.
 */
@Composable
private fun PaginationEffect(
    listState: LazyListState,
    hasMessages: Boolean,
    isPaginatedLoading: Boolean,
    onLoadMore: () -> Unit
) {
    // Use derivedStateOf to only recompute when firstVisibleItemIndex changes
    val shouldLoadMore by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && hasMessages && !isPaginatedLoading
        }
    }
    
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }
}

/**
 * Scrolls to bottom when new messages arrive with smooth animation.
 */
@Composable
private fun ScrollToBottomEffect(
    listState: LazyListState,
    messagesSize: Int
) {
    LaunchedEffect(messagesSize) {
        if (messagesSize > 0) {
            listState.animateScrollToItem(
                index = messagesSize - 1,
                scrollOffset = 0
            )
        }
    }
}

/**
 * Stateless chat screen content - pure UI rendering without side effects.
 * 
 * All state is passed down as parameters, and all events are hoisted via callbacks.
 * This makes the composable testable and prevents unnecessary recompositions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreenContent(
    state: ChatState,
    listState: LazyListState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onMessageInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onPickMedia: () -> Unit,
    onClearMedia: () -> Unit,
    onDeleteMessage: (String) -> Unit,
    onRetryMessage: (Message) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ChatTopBar(
                userName = state.currentUserName,
                onBack = onBack
            )
        },
        bottomBar = {
            ChatBottomBar(
                messageInputText = state.messageInputText,
                selectedMediaUris = state.selectedMediaUris,
                typingUsers = state.typingUsers,
                canSend = state.canSendMessage,
                onMessageInputChange = onMessageInputChange,
                onSendMessage = onSendMessage,
                onPickMedia = onPickMedia,
                onClearMedia = onClearMedia
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        ChatMessagesList(
            messages = state.messages,
            currentUserId = state.currentUserId,
            isLoading = state.isLoading,
            isPaginatedLoading = state.isPaginatedLoading,
            listState = listState,
            paddingValues = paddingValues,
            onDeleteMessage = onDeleteMessage,
            onRetryMessage = onRetryMessage
        )
    }
}

/**
 * Chat top app bar - displays user info and back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    userName: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(username = userName, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = userName.ifEmpty { stringResource(R.string.chat_title) },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.online),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                interactionSource = remember { MutableInteractionSource() },
                modifier = Modifier.semantics {
                    contentDescription = "Navigate back"
                }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

/**
 * Chat bottom bar - contains typing indicator and message input.
 */
@Composable
private fun ChatBottomBar(
    messageInputText: String,
    selectedMediaUris: List<String>,
    typingUsers: List<String>,
    canSend: Boolean,
    onMessageInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onPickMedia: () -> Unit,
    onClearMedia: () -> Unit
) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        // Typing indicator with fade-in animation
        AnimatedVisibility(
            visible = typingUsers.isNotEmpty(),
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 300
                )
            ) + expandVertically(
                animationSpec = tween(
                    durationMillis = 300
                )
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 300
                )
            ) + shrinkVertically(
                animationSpec = tween(
                    durationMillis = 300
                )
            )
        ) {
            Text(
                text = stringResource(R.string.typing_indicator, typingUsers.joinToString(", ")),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 4.dp)
                    .animateContentSize()
                    .semantics {
                        contentDescription = "${typingUsers.joinToString(", ")} is typing"
                    }
            )
        }
        
        // Message input field - stateless
        MessageInputField(
            text = messageInputText,
            onTextChange = onMessageInputChange,
            selectedMediaUris = selectedMediaUris,
            canSend = canSend,
            onSendMessage = onSendMessage,
            onPickMedia = onPickMedia,
            onClearMedia = onClearMedia
        )
    }
}

/**
 * Messages list with pagination loading indicator.
 */
@Composable
private fun ChatMessagesList(
    messages: List<Message>,
    currentUserId: String,
    isLoading: Boolean,
    isPaginatedLoading: Boolean,
    listState: LazyListState,
    paddingValues: PaddingValues,
    onDeleteMessage: (String) -> Unit,
    onRetryMessage: (Message) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Show empty state when no messages and not loading
        if (messages.isEmpty() && !isLoading) {
            EmptyState(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
            ) {
                if (isPaginatedLoading) {
                    item(key = "pagination_loader") {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(
                                animationSpec = tween(durationMillis = 300)
                            ),
                            exit = fadeOut(
                                animationSpec = tween(durationMillis = 300)
                            )
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }

                items(
                    items = messages,
                    key = { it.id }
                ) { message ->
                    MessageItem(
                        message = message,
                        isOwnMessage = message.senderId == currentUserId,
                        onDelete = onDeleteMessage,
                        onRetry = onRetryMessage
                    )
                }
            }
        }

        // Main loading indicator with smooth fade
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 300)
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier.align(Alignment.Center)
        ) {
            CircularProgressIndicator()
        }
    }
}
