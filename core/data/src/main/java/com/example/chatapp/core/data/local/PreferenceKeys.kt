package com.example.chatapp.core.data.local

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val USERNAME = stringPreferencesKey("username")
    val DEVICE_ID = stringPreferencesKey("device_id")
}
