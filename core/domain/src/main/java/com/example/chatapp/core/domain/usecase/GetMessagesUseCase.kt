package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.repository.IMessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: IMessageRepository
) {
    operator fun invoke(): Flow<List<Message>> {
        return repository.getMessages()
    }

    suspend fun getOlder(lastTimestamp: Long, limit: Int = 20): List<Message> {
        return repository.getOlderMessages(lastTimestamp, limit)
    }
}
