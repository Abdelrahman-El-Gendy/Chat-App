package com.example.chatapp.feature.auth_identity.presentation.model

import com.example.chatapp.core.ui.mvi.UiEffect

sealed class SettingsEffect : UiEffect {
    object ShowConfirmClear : SettingsEffect()
    object NavigateToLogin : SettingsEffect()
    object RestartApp : SettingsEffect()
    data class ShowError(val message: String) : SettingsEffect()
}
