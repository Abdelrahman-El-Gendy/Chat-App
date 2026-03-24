package com.example.chatapp.feature.auth_identity.presentation.model

import com.example.chatapp.core.ui.mvi.UiEffect

sealed class ProfileEffect : UiEffect {
    data class ShowSuccess(val message: String) : ProfileEffect()
    data class ShowError(val message: String) : ProfileEffect()
    object NavigateBack : ProfileEffect()
    object NavigateToLogin : ProfileEffect()
}
