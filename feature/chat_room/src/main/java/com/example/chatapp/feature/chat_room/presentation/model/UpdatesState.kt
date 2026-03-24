package com.example.chatapp.feature.chat_room.presentation.model

import androidx.compose.runtime.Immutable
import com.example.chatapp.core.ui.mvi.UiState

data class StatusStory(
    val id: String,
    val userName: String,
    val avatarUrl: String? = null,
    val timestamp: Long,
    val isViewed: Boolean = false,
    val isMine: Boolean = false
)

data class ChannelUpdate(
    val id: String,
    val channelName: String,
    val description: String,
    val imageUrl: String? = null,
    val isFollowing: Boolean = false
)

data class SuggestedUser(
    val id: String,
    val name: String,
    val description: String,
    val avatarUrl: String? = null
)

@Immutable
data class UpdatesState(
    val myStatus: StatusStory? = null,
    val recentStatuses: List<StatusStory> = emptyList(),
    val channels: List<ChannelUpdate> = emptyList(),
    val recentUpdates: List<ChannelUpdate> = emptyList(),
    val suggestedUsers: List<SuggestedUser> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) : UiState
