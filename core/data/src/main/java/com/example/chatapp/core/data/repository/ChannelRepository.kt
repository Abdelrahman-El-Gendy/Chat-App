package com.example.chatapp.core.data.repository

import com.example.chatapp.core.data.remote.FirebaseChannelService
import com.example.chatapp.core.domain.model.Channel
import com.example.chatapp.core.domain.repository.IChannelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChannelRepository @Inject constructor(
    private val firebaseChannelService: FirebaseChannelService
) : IChannelRepository {

    override fun getChannels(): Flow<List<Channel>> = firebaseChannelService.getChannels()

    override suspend fun createChannel(name: String, description: String): Result<Channel> {
        return try {
            val channel = firebaseChannelService.createChannel(name, description)
            Result.success(channel)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinChannel(channelId: String): Result<Unit> {
        // For now, channels are public — no join needed
        return Result.success(Unit)
    }

    override suspend fun leaveChannel(channelId: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getChannel(channelId: String): Channel? {
        return firebaseChannelService.getChannel(channelId)
    }

    suspend fun seedDefaultChannel() {
        firebaseChannelService.seedDefaultChannel()
    }
}
