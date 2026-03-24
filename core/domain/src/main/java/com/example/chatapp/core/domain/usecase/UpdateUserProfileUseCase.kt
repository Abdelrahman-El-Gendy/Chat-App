package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.model.User
import com.example.chatapp.core.domain.repository.IUserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> = userRepository.updateProfile(user)
}
