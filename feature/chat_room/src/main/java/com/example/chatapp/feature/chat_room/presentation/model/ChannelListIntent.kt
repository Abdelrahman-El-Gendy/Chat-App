package com.example.chatapp.feature.chat_room.presentation.model

import com.example.chatapp.core.ui.mvi.UiIntent

sealed class ChannelListIntent : UiIntent {
    object LoadChannels : ChannelListIntent()
    data class SelectChannel(val channelId: String) : ChannelListIntent()
    data class CreateChannel(val name: String) : ChannelListIntent()
    object Refresh : ChannelListIntent()
}
