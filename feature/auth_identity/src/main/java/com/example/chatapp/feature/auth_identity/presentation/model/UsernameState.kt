package com.example.chatapp.feature.auth_identity.presentation.model

import com.example.chatapp.core.ui.mvi.UiState

/**
 * UI State for Username Screen.
 * 
 * Contains all data needed to render the username input interface.
 */
data class UsernameState(
    /**
     * The saved username (empty if not yet set)
     */
    val username: String = "",
    
    /**
     * Current text in the input field
     */
    val inputText: String = "",
    
    /**
     * Validation error message, null if no error
     */
    val error: String? = null,
    
    /**
     * Whether a save operation is in progress
     */
    val isLoading: Boolean = false
) : UiState {
    
    /**
     * Check if user already has a username set
     */
    val hasUsername: Boolean
        get() = username.isNotEmpty()
    
    /**
     * Check if the input is valid for submission
     */
    val isInputValid: Boolean
        get() = inputText.trim().length in 3..20
    
    /**
     * Check if there's a validation error
     */
    val hasError: Boolean
        get() = error != null
}
