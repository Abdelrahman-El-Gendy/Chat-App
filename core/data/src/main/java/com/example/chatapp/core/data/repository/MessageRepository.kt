package com.example.chatapp.core.data.repository

import com.example.chatapp.core.data.remote.FirebaseMessageService
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.repository.IMessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

import com.example.chatapp.core.domain.repository.IWorkScheduler
import androidx.core.net.toUri

class MessageRepository @Inject constructor(
    private val firebaseService: FirebaseMessageService,
    private val workScheduler: IWorkScheduler,
    private val contentUriCopier: com.example.chatapp.core.data.util.ContentUriCopier
) : IMessageRepository {
    override fun getMessages(): Flow<List<Message>> = firebaseService.getMessages()

    override suspend fun sendMessage(message: Message) {
        firebaseService.sendMessage(message)
    }

    override suspend fun getOlderMessages(lastTimestamp: Long, limit: Int): List<Message> {
        return firebaseService.getOlderMessages(lastTimestamp, limit)
    }

    override suspend fun queueMessage(text: String?, mediaUris: List<String>?, senderId: String, senderName: String) {
        val finalMediaUris = if (!mediaUris.isNullOrEmpty()) {
            mediaUris.mapNotNull { uriString ->
                contentUriCopier.copyToInternalStorage(uriString.toUri())?.toString()
            }
        } else {
            mediaUris
        }

        val messageId = java.util.UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val initialMessage = Message(
            id = messageId,
            text = text,
            mediaUrls = finalMediaUris,
            senderId = senderId,
            senderName = senderName,
            timestamp = timestamp,
            status = com.example.chatapp.core.domain.model.MessageStatus.SENDING
        )
        
        // Send initial message with SENDING status for immediate UI feedback
        firebaseService.sendMessageNonSuspend(initialMessage)
        
        // Schedule worker to update status to SENT after successful delivery
        workScheduler.scheduleMessageSend(messageId, text, finalMediaUris, senderId, senderName, timestamp)
    }

    override suspend fun deleteMessage(messageId: String) {
        firebaseService.deleteMessage(messageId)
    }

    override fun getTypingUsers(): Flow<List<String>> = firebaseService.getTypingUsers()
}
