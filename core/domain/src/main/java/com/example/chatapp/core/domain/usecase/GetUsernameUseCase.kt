package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.model.User
import com.example.chatapp.core.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsernameUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    operator fun invoke(): Flow<String?> {
        return repository.getUsername()
    }
}
