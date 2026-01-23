package com.example.chatapp.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager @Inject constructor(
    private val context: Context
) {
    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.USERNAME] = username
        }
    }

    fun getUsername(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.USERNAME]
        }
    }

    suspend fun saveDeviceId(deviceId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DEVICE_ID] = deviceId
        }
    }

    fun getDeviceId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.DEVICE_ID]
        }
    }
}
