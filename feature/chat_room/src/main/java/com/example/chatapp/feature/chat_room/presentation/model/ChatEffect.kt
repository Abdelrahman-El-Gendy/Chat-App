package com.example.chatapp.feature.chat_room.presentation.model

import com.example.chatapp.core.ui.mvi.UiEffect

/**
 * Side Effects for Chat Screen.
 * 
 * Represents one-time events that should not be part of the state.
 * These are consumed once and not re-emitted on configuration changes.
 */
sealed class ChatEffect : UiEffect {
    
    /**
     * Show an error message to the user
     * 
     * @param message The error message to display
     */
    data class ShowError(val message: String) : ChatEffect()
    
    /**
     * Scroll the message list to the bottom
     */
    object ScrollToBottom : ChatEffect()
    
    /**
     * Show a toast message
     * 
     * @param message The toast message to display
     */
    data class ShowToast(val message: String) : ChatEffect()
    
    /**
     * Navigate back (close the chat screen)
     */
    object NavigateBack : ChatEffect()
}
