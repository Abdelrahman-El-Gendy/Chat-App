package com.example.chatapp.core.domain.repository

import com.example.chatapp.core.domain.model.Channel
import kotlinx.coroutines.flow.Flow

interface IChannelRepository {
    fun getChannels(): Flow<List<Channel>>
    suspend fun createChannel(name: String, description: String = ""): Result<Channel>
    suspend fun joinChannel(channelId: String): Result<Unit>
    suspend fun leaveChannel(channelId: String): Result<Unit>
    suspend fun getChannel(channelId: String): Channel?
}
