package com.example.chatapp.core.domain.repository

import com.example.chatapp.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun getUsername(): Flow<String?>
    suspend fun saveUsername(username: String)
    fun getDeviceId(): Flow<String>
    suspend fun saveDeviceId(deviceId: String)
    suspend fun setTypingStatus(isTyping: Boolean)
}
