package com.example.chatapp.core.data.repository

import com.example.chatapp.core.data.remote.FirebaseMessageService
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.repository.IMessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

import com.example.chatapp.core.domain.repository.IWorkScheduler

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
                contentUriCopier.copyToInternalStorage(android.net.Uri.parse(uriString))?.toString()
            }
        } else {
            mediaUris
        }

        val messageId = java.util.UUID.randomUUID().toString()
        val initialMessage = Message(
            id = messageId,
            text = text,
            mediaUrls = finalMediaUris,
            senderId = senderId,
            senderName = senderName,
            status = com.example.chatapp.core.domain.model.MessageStatus.SENDING
        )
        
        // Use a coroutine or just a fire-and-forget to insert initial state
        // Since we are in a non-suspend function, we might need a scope or just call a non-suspend firebase method
        firebaseService.sendMessageNonSuspend(initialMessage)
        
        workScheduler.scheduleMessageSend(messageId, text, finalMediaUris, senderId, senderName)
    }

    override suspend fun deleteMessage(messageId: String) {
        firebaseService.deleteMessage(messageId)
    }

    override fun getTypingUsers(): Flow<List<String>> = firebaseService.getTypingUsers()
}
