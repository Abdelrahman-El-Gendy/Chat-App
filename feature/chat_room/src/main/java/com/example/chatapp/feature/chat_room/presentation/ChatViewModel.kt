package com.example.chatapp.feature.chat_room.presentation

import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.usecase.*
import com.example.chatapp.core.ui.mvi.BaseMviViewModel
import com.example.chatapp.feature.chat_room.presentation.model.ChatEffect
import com.example.chatapp.feature.chat_room.presentation.model.ChatIntent
import com.example.chatapp.feature.chat_room.presentation.model.ChatState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Chat Screen following MVI architecture.
 * 
 * Handles all business logic for the chat feature including:
 * - Loading and observing messages
 * - Sending messages with optional media
 * - Deleting messages
 * - Managing typing indicators
 * - Pagination for older messages
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getUsernameUseCase: GetUsernameUseCase,
    private val getDeviceIdUseCase: GetDeviceIdUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val getTypingUsersUseCase: GetTypingUsersUseCase,
    private val setTypingStatusUseCase: SetTypingStatusUseCase
) : BaseMviViewModel<ChatState, ChatIntent, ChatEffect>(
    initialState = ChatState()
) {

    init {
        observeUserInfo()
        observeMessages()
        observeTypingUsers()
    }

    override suspend fun handleIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.SendMessage -> sendMessage(intent.text, intent.mediaUris)
            is ChatIntent.DeleteMessage -> deleteMessage(intent.messageId)
            is ChatIntent.RetryMessage -> retryMessage(intent.message)
            is ChatIntent.LoadMoreMessages -> loadMoreMessages()
            is ChatIntent.SetTyping -> setTyping(intent.isTyping)
            is ChatIntent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun observeUserInfo() {
        viewModelScope.launch {
            combine(getUsernameUseCase(), getDeviceIdUseCase()) { name, id ->
                name to id
            }.collect { (name, id) ->
                setState {
                    copy(
                        currentUserName = name ?: "Anonymous",
                        currentUserId = id
                    )
                }
            }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            getMessagesUseCase()
                .catch { e ->
                    setState { copy(isLoading = false, error = e.message) }
                    setEffect(ChatEffect.ShowError(e.message ?: "Unknown error"))
                }
                .collect { messages ->
                    val wasEmpty = currentState.messages.isEmpty()
                    setState { copy(messages = messages, isLoading = false) }
                    if (wasEmpty && messages.isNotEmpty()) {
                        setEffect(ChatEffect.ScrollToBottom)
                    }
                }
        }
    }

    private fun observeTypingUsers() {
        viewModelScope.launch {
            getTypingUsersUseCase().collect { users ->
                val others = users.filter { it != currentState.currentUserName }
                setState { copy(typingUsers = others) }
            }
        }
    }

    private suspend fun sendMessage(text: String?, mediaUris: List<String>?) {
        try {
            sendMessageUseCase(
                text = text,
                mediaUris = mediaUris,
                senderId = currentState.currentUserId,
                senderName = currentState.currentUserName
            )
        } catch (e: Exception) {
            setState { copy(error = "Failed to send message: ${e.message}") }
            setEffect(ChatEffect.ShowError("Failed to send message"))
        }
    }

    private suspend fun deleteMessage(messageId: String) {
        try {
            deleteMessageUseCase(messageId)
        } catch (e: Exception) {
            setEffect(ChatEffect.ShowError("Failed to delete message"))
        }
    }

    private suspend fun retryMessage(message: Message) {
        deleteMessage(message.id)
        sendMessage(message.text, message.mediaUrls)
    }

    private suspend fun loadMoreMessages() {
        if (!currentState.hasMoreMessages || currentState.isPaginatedLoading) return

        val lastTimestamp = currentState.messages.firstOrNull()?.timestamp ?: return

        setState { copy(isPaginatedLoading = true) }

        try {
            val olderMessages = getMessagesUseCase.getOlder(lastTimestamp)
            if (olderMessages.isEmpty()) {
                setState { copy(hasMoreMessages = false, isPaginatedLoading = false) }
            } else {
                setState {
                    copy(
                        messages = (olderMessages + messages).distinctBy { it.id },
                        isPaginatedLoading = false
                    )
                }
            }
        } catch (e: Exception) {
            setState { copy(isPaginatedLoading = false, error = e.message) }
        }
    }

    private suspend fun setTyping(isTyping: Boolean) {
        try {
            setTypingStatusUseCase(isTyping)
        } catch (e: Exception) {
            // Silently fail for typing status
        }
    }
}
