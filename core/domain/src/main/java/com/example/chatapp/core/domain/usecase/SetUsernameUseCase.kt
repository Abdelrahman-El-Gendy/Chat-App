package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.repository.IUserRepository
import javax.inject.Inject

class SetUsernameUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    suspend operator fun invoke(username: String) {
        repository.saveUsername(username)
    }
}
