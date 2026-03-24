package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.model.Channel
import com.example.chatapp.core.domain.repository.IChannelRepository
import javax.inject.Inject

class CreateChannelUseCase @Inject constructor(
    private val channelRepository: IChannelRepository
) {
    suspend operator fun invoke(name: String, description: String = ""): Result<Channel> =
        channelRepository.createChannel(name, description)
}
