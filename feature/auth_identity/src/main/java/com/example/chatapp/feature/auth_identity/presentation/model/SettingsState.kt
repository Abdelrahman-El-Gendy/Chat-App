package com.example.chatapp.feature.auth_identity.presentation.model

import androidx.compose.runtime.Immutable
import com.example.chatapp.core.ui.mvi.UiState

@Immutable
data class SettingsState(
    val notificationsEnabled: Boolean = true,
    val isDarkTheme: Boolean = false,
    val appVersion: String = "1.0.0",
    val isLoading: Boolean = true
) : UiState
