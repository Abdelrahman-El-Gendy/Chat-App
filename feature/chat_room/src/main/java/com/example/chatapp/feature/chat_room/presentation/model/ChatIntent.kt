package com.example.chatapp.feature.chat_room.presentation.model

import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.ui.mvi.UiIntent

/**
 * User Intents for Chat Screen.
 * 
 * Represents all possible user actions that can be performed
 * on the chat screen. Each intent triggers a specific behavior
 * in the ViewModel.
 */
sealed class ChatIntent : UiIntent {
    
    /**
     * User wants to send a message
     * 
     * @param text The message text (can be null if only sending media)
     * @param mediaUris List of media URIs to attach (can be null or empty)
     */
    data class SendMessage(
        val text: String?,
        val mediaUris: List<String>? = null
    ) : ChatIntent()
    
    /**
     * User wants to delete a message
     * 
     * @param messageId The ID of the message to delete
     */
    data class DeleteMessage(val messageId: String) : ChatIntent()
    
    /**
     * User wants to retry sending a failed message
     * 
     * @param message The failed message to retry
     */
    data class RetryMessage(val message: Message) : ChatIntent()
    
    /**
     * User scrolled to top, load more (older) messages
     */
    object LoadMoreMessages : ChatIntent()
    
    /**
     * User typing status changed
     * 
     * @param isTyping Whether the user is currently typing
     */
    data class SetTyping(val isTyping: Boolean) : ChatIntent()
    
    /**
     * Clear the current error message
     */
    object ClearError : ChatIntent()
}
