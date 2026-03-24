package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.repository.IUserRepository
import javax.inject.Inject

class UploadAvatarUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(uriString: String): Result<String> =
        userRepository.uploadAvatar(uriString)
}
