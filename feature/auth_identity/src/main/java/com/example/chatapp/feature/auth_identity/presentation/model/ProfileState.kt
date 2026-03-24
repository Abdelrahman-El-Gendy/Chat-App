package com.example.chatapp.feature.auth_identity.presentation.model

import androidx.compose.runtime.Immutable
import com.example.chatapp.core.ui.mvi.UiState

@Immutable
data class ProfileState(
    val username: String = "",
    val avatarUrl: String? = null,
    val selectedAvatarUri: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val deviceId: String = ""
) : UiState
