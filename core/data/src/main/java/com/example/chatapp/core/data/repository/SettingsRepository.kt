package com.example.chatapp.core.data.repository

import com.example.chatapp.core.data.local.SettingsDataStore
import com.example.chatapp.core.domain.model.AppSettings
import com.example.chatapp.core.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ISettingsRepository {
    override fun getSettings(): Flow<AppSettings> = settingsDataStore.getSettings()
    override suspend fun updateNotifications(enabled: Boolean) = settingsDataStore.updateNotifications(enabled)
    override suspend fun updateTheme(isDark: Boolean) = settingsDataStore.updateTheme(isDark)
    override suspend fun clearAll() = settingsDataStore.clearAll()
}
