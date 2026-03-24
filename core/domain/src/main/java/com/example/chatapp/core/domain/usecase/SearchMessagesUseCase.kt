package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.repository.IMessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMessagesUseCase @Inject constructor(
    private val messageRepository: IMessageRepository
) {
    operator fun invoke(query: String): Flow<List<Message>> =
        messageRepository.searchMessages(query)
}
