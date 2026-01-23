package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.repository.IMessageRepository
import javax.inject.Inject

class DeleteMessageUseCase @Inject constructor(
    private val repository: IMessageRepository
) {
    suspend operator fun invoke(messageId: String) {
        repository.deleteMessage(messageId)
    }
}
