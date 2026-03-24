package com.example.chatapp.core.domain.repository

import com.example.chatapp.core.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateNotifications(enabled: Boolean)
    suspend fun updateTheme(isDark: Boolean)
    suspend fun clearAll()
}
