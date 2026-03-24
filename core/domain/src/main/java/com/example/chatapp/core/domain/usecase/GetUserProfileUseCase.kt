package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.model.User
import com.example.chatapp.core.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): Flow<User> = userRepository.getUserProfile()
}
