package com.example.chatapp.feature.auth_identity.presentation

import androidx.lifecycle.viewModelScope
import com.example.chatapp.core.domain.repository.ISettingsRepository
import com.example.chatapp.core.ui.mvi.BaseMviViewModel
import com.example.chatapp.feature.auth_identity.presentation.model.SettingsEffect
import com.example.chatapp.feature.auth_identity.presentation.model.SettingsIntent
import com.example.chatapp.feature.auth_identity.presentation.model.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: ISettingsRepository
) : BaseMviViewModel<SettingsState, SettingsIntent, SettingsEffect>(
    initialState = SettingsState()
) {

    init {
        loadSettings()
    }

    override suspend fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToggleNotifications -> toggleNotifications(intent.enabled)
            is SettingsIntent.ChangeTheme -> changeTheme(intent.isDark)
            is SettingsIntent.ClearLocalData -> clearLocalData()
            is SettingsIntent.Logout -> logout()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                setState {
                    copy(
                        notificationsEnabled = settings.notificationsEnabled,
                        isDarkTheme = settings.isDarkTheme,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun toggleNotifications(enabled: Boolean) {
        setState { copy(notificationsEnabled = enabled) }
        settingsRepository.updateNotifications(enabled)
    }

    private suspend fun changeTheme(isDark: Boolean) {
        setState { copy(isDarkTheme = isDark) }
        settingsRepository.updateTheme(isDark)
    }

    private suspend fun clearLocalData() {
        setEffect(SettingsEffect.ShowConfirmClear)
        settingsRepository.clearAll()
        setEffect(SettingsEffect.NavigateToLogin)
    }

    private fun logout() {
        setEffect(SettingsEffect.NavigateToLogin)
    }
}
