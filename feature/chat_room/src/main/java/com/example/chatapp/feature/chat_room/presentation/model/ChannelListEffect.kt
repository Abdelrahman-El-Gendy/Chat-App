package com.example.chatapp.feature.chat_room.presentation.model

import com.example.chatapp.core.ui.mvi.UiEffect

sealed class ChannelListEffect : UiEffect {
    data class NavigateToChat(val channelId: String) : ChannelListEffect()
    data class ShowError(val message: String) : ChannelListEffect()
}
