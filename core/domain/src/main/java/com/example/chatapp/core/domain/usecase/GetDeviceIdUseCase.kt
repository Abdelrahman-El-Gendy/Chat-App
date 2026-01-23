package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDeviceIdUseCase @Inject constructor(
    private val repository: IUserRepository
) {
    operator fun invoke(): Flow<String> = repository.getDeviceId()
}
