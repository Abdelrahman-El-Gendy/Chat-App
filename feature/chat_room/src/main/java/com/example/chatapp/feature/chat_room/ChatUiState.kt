package com.example.chatapp.feature.chat_room

import com.example.chatapp.core.domain.model.Message

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val isPaginatedLoading: Boolean = false,
    val error: String? = null,
    val currentUser: String = "",
    val currentUserName: String = "",
    val hasMoreMessages: Boolean = true,
    val typingUsers: List<String> = emptyList()
)
