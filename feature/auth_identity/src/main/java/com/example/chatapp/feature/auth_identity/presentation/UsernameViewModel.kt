package com.example.chatapp.feature.auth_identity.presentation

import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.domain.usecase.GetUsernameUseCase
import com.example.chatapp.core.domain.usecase.SetUsernameUseCase
import com.example.chatapp.core.ui.mvi.BaseMviViewModel
import com.example.chatapp.feature.auth_identity.presentation.model.UsernameEffect
import com.example.chatapp.feature.auth_identity.presentation.model.UsernameIntent
import com.example.chatapp.feature.auth_identity.presentation.model.UsernameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Username Screen following MVI architecture.
 * 
 * Handles username validation, saving, and navigation logic.
 */
@HiltViewModel
class UsernameViewModel @Inject constructor(
    private val getUsernameUseCase: GetUsernameUseCase,
    private val setUsernameUseCase: SetUsernameUseCase
) : BaseMviViewModel<UsernameState, UsernameIntent, UsernameEffect>(
    initialState = UsernameState()
) {

    init {
        observeUsername()
    }

    override suspend fun handleIntent(intent: UsernameIntent) {
        when (intent) {
            is UsernameIntent.UpdateInputText -> {
                setState { copy(inputText = intent.text, error = null) }
            }
            is UsernameIntent.SubmitUsername -> {
                submitUsername(intent.username)
            }
            is UsernameIntent.ClearError -> {
                setState { copy(error = null) }
            }
        }
    }

    private fun observeUsername() {
        viewModelScope.launch {
            getUsernameUseCase().collect { username ->
                setState { copy(username = username ?: "") }
                if (!username.isNullOrEmpty()) {
                    setEffect(UsernameEffect.NavigateToChat)
                }
            }
        }
    }

    private suspend fun submitUsername(username: String) {
        val trimmed = username.trim()

        if (trimmed.length !in 3..20) {
            setState { copy(error = "Username must be 3-20 characters") }
            return
        }

        setState { copy(isLoading = true) }

        try {
            setUsernameUseCase(trimmed)
            setState { copy(isLoading = false) }
        } catch (e: Exception) {
            setState { copy(isLoading = false, error = e.message) }
            setEffect(UsernameEffect.ShowError(e.message ?: "Failed to save username"))
        }
    }
}
