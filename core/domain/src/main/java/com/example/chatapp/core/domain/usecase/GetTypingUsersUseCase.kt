package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.repository.IMessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTypingUsersUseCase @Inject constructor(
    private val repository: IMessageRepository
) {
    operator fun invoke(): Flow<List<String>> = repository.getTypingUsers()
}
