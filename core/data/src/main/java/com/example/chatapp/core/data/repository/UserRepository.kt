package com.example.chatapp.core.data.repository

import android.content.Context
import android.provider.Settings
import com.example.chatapp.core.data.local.DataStoreManager
import com.example.chatapp.core.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    @ApplicationContext private val context: Context,
    private val database: FirebaseDatabase
) : IUserRepository {
    private val typingRef = database.getReference("typing")
    private val usernameFlow = dataStoreManager.getUsername()
    private val deviceIdFlow = getDeviceId()

    override fun getUsername(): Flow<String?> = dataStoreManager.getUsername()

    override suspend fun saveUsername(username: String) {
        dataStoreManager.saveUsername(username)
    }

    override fun getDeviceId(): Flow<String> = dataStoreManager.getDeviceId().map { 
        it ?: Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    override suspend fun saveDeviceId(deviceId: String) {
        dataStoreManager.saveDeviceId(deviceId)
    }

    override suspend fun setTypingStatus(isTyping: Boolean) {
        val deviceId = dataStoreManager.getDeviceId().firstOrNull() ?: Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val username = dataStoreManager.getUsername().firstOrNull() ?: "Anonymous"
        
        if (isTyping) {
            typingRef.child(deviceId).setValue(username).await()
            typingRef.child(deviceId).onDisconnect().removeValue()
        } else {
            typingRef.child(deviceId).removeValue().await()
        }
    }
}
