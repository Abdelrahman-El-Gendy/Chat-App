package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.repository.IMessageRepository
import javax.inject.Inject

class MarkMessagesReadUseCase @Inject constructor(
    private val messageRepository: IMessageRepository
) {
    suspend operator fun invoke(channelId: String, upToTimestamp: Long) =
        messageRepository.markAsRead(channelId, upToTimestamp)
}
