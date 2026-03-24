package com.example.chatapp.feature.auth_identity.presentation.model

import com.example.chatapp.core.ui.mvi.UiIntent

sealed class SettingsIntent : UiIntent {
    data class ToggleNotifications(val enabled: Boolean) : SettingsIntent()
    data class ChangeTheme(val isDark: Boolean) : SettingsIntent()
    object ClearLocalData : SettingsIntent()
    object Logout : SettingsIntent()
}
