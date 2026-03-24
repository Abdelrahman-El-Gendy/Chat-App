package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.model.Channel
import com.example.chatapp.core.domain.repository.IChannelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChannelsUseCase @Inject constructor(
    private val channelRepository: IChannelRepository
) {
    operator fun invoke(): Flow<List<Channel>> = channelRepository.getChannels()
}
