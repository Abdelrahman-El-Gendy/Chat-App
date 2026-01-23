package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.repository.IUserRepository
import javax.inject.Inject

class SetTypingStatusUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    suspend operator fun invoke(isTyping: Boolean) {
        repository.setTypingStatus(isTyping)
    }
}
