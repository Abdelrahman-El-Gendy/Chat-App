package com.example.chatapp.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.chatapp.core.domain.model.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
    }

    fun getSettings(): Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            isDarkTheme = prefs[Keys.IS_DARK_THEME] ?: false
        )
    }

    suspend fun updateNotifications(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateTheme(isDark: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.IS_DARK_THEME] = isDark
        }
    }

    suspend fun clearAll() {
        context.settingsDataStore.edit { it.clear() }
    }
}
