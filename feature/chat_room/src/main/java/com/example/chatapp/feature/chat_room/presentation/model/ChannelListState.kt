package com.example.chatapp.feature.chat_room.presentation.model

import androidx.compose.runtime.Immutable
import com.example.chatapp.core.domain.model.Channel
import com.example.chatapp.core.ui.mvi.UiState

@Immutable
data class ChannelListState(
    val channels: List<Channel> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val showCreateDialog: Boolean = false
) : UiState
