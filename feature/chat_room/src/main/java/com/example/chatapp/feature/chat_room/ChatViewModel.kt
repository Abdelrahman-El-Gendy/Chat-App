package com.example.chatapp.feature.chat_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.domain.usecase.GetMessagesUseCase
import com.example.chatapp.core.domain.usecase.SendMessageUseCase
import com.example.chatapp.core.domain.usecase.GetUsernameUseCase
import com.example.chatapp.core.domain.usecase.GetDeviceIdUseCase
import com.example.chatapp.core.domain.usecase.DeleteMessageUseCase
import com.example.chatapp.core.domain.usecase.GetTypingUsersUseCase
import com.example.chatapp.core.domain.usecase.SetTypingStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getUsernameUseCase: GetUsernameUseCase,
    private val getDeviceIdUseCase: GetDeviceIdUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val getTypingUsersUseCase: GetTypingUsersUseCase,
    private val setTypingStatusUseCase: SetTypingStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    init {
        loadUserInfo()
        loadMessages()
        observeTypingUsers()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            combine(getUsernameUseCase(), getDeviceIdUseCase()) { name, id ->
                name to id
            }.collect { (name, id) ->
                _uiState.update { it.copy(currentUserName = name ?: "Anonymous", currentUser = id) }
            }
        }
    }

    private fun loadMessages() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                getMessagesUseCase()
                    .catch { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                    .collect { messages ->
                        _uiState.update { it.copy(messages = messages, isLoading = false) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadMoreMessages() {
        if (!_uiState.value.hasMoreMessages || _uiState.value.isPaginatedLoading) return
        
        val lastTimestamp = _uiState.value.messages.firstOrNull()?.timestamp ?: return

        _uiState.update { it.copy(isPaginatedLoading = true) }
        viewModelScope.launch {
            try {
                val olderMessages = getMessagesUseCase.getOlder(lastTimestamp)
                if (olderMessages.isEmpty()) {
                    _uiState.update { it.copy(hasMoreMessages = false, isPaginatedLoading = false) }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            messages = (olderMessages + state.messages).distinctBy { it.id },
                            isPaginatedLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isPaginatedLoading = false, error = e.message) }
            }
        }
    }

    fun sendMessage(text: String?, mediaUris: List<String>? = null) {
        val state = _uiState.value
        val currentUser = state.currentUser
        val currentUserName = state.currentUserName

        viewModelScope.launch {
            try {
                sendMessageUseCase(
                    text = text,
                    mediaUris = mediaUris,
                    senderId = currentUser,
                    senderName = currentUserName
                )
            } catch (e: Exception) {
                // Handle potential errors from suspend call
                _uiState.update { it.copy(error = "Failed to send message: ${e.message}") }
            }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            deleteMessageUseCase(messageId)
        }
    }

    private fun observeTypingUsers() {
        viewModelScope.launch {
            getTypingUsersUseCase().collect { users ->
                val others = users.filter { it != _uiState.value.currentUserName }
                _uiState.update { it.copy(typingUsers = others) }
            }
        }
    }

    fun setTyping(isTyping: Boolean) {
        viewModelScope.launch {
            setTypingStatusUseCase(isTyping)
        }
    }
}
