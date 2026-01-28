package com.example.chatapp.feature.auth_identity.presentation.model

import com.example.chatapp.core.ui.mvi.UiEffect

/**
 * Side Effects for Username Screen.
 * 
 * Represents one-time events that should not be part of the state.
 */
sealed class UsernameEffect : UiEffect {
    
    /**
     * Navigate to the chat screen after successful username setup
     */
    object NavigateToChat : UsernameEffect()
    
    /**
     * Show an error message to the user
     * 
     * @param message The error message to display
     */
    data class ShowError(val message: String) : UsernameEffect()
}
