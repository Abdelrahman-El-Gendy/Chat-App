package com.example.chatapp.feature.chat_room.presentation.model

import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.ui.mvi.UiState

/**
 * UI State for Chat Screen.
 * 
 * Contains all data needed to render the chat interface.
 * This is an immutable data class that represents the complete
 * state of the chat screen at any given moment.
 */
data class ChatState(
    /**
     * List of messages in the chat
     */
    val messages: List<Message> = emptyList(),
    
    /**
     * Whether initial messages are being loaded
     */
    val isLoading: Boolean = true,
    
    /**
     * Whether older messages are being loaded (pagination)
     */
    val isPaginatedLoading: Boolean = false,
    
    /**
     * Error message to display, null if no error
     */
    val error: String? = null,
    
    /**
     * Current user's unique identifier
     */
    val currentUserId: String = "",
    
    /**
     * Current user's display name
     */
    val currentUserName: String = "",
    
    /**
     * Whether there are more older messages to load
     */
    val hasMoreMessages: Boolean = true,
    
    /**
     * List of users currently typing (excluding current user)
     */
    val typingUsers: List<String> = emptyList()
) : UiState {
    
    /**
     * Check if the chat is empty (no messages)
     */
    val isEmpty: Boolean
        get() = messages.isEmpty() && !isLoading
    
    /**
     * Check if there's an active error
     */
    val hasError: Boolean
        get() = error != null
}
