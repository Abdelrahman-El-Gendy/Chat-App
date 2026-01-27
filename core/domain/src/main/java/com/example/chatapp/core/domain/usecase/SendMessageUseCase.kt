package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.repository.IMessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: IMessageRepository
) {
    suspend operator fun invoke(text: String?, mediaUris: List<String>?, senderId: String, senderName: String) {
        repository.queueMessage(text, mediaUris, senderId, senderName)
    }

    suspend fun sendDirectly(message: Message) {
        repository.sendMessage(message)
    }
}
