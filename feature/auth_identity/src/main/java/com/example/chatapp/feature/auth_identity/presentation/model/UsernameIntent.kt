package com.example.chatapp.feature.auth_identity.presentation.model

import com.example.chatapp.core.ui.mvi.UiIntent

/**
 * User Intents for Username Screen.
 * 
 * Represents all possible user actions on the username screen.
 */
sealed class UsernameIntent : UiIntent {
    
    /**
     * User updated the text in the input field
     * 
     * @param text The new text value
     */
    data class UpdateInputText(val text: String) : UsernameIntent()
    
    /**
     * User submitted the username
     * 
     * @param username The username to save
     */
    data class SubmitUsername(val username: String) : UsernameIntent()
    
    /**
     * Clear the current error message
     */
    object ClearError : UsernameIntent()
}
