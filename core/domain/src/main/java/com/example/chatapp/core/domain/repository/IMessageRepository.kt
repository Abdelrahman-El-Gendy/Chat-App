package com.example.chatapp.core.domain.repository

import com.example.chatapp.core.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface IMessageRepository {
    fun getMessages(): Flow<List<Message>>
    suspend fun sendMessage(message: Message)
    suspend fun getOlderMessages(lastTimestamp: Long, limit: Int): List<Message>
    fun queueMessage(text: String?, mediaUris: List<String>?, senderId: String, senderName: String)
    suspend fun deleteMessage(messageId: String)
    fun getTypingUsers(): Flow<List<String>>
}
