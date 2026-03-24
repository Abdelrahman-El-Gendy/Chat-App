package com.example.chatapp.feature.chat_room.presentation.model

import com.example.chatapp.core.ui.mvi.UiIntent

sealed class UpdatesIntent : UiIntent {
    object LoadUpdates : UpdatesIntent()
    data class ViewStatus(val storyId: String) : UpdatesIntent()
    object CreateStatus : UpdatesIntent()
    data class FollowChannel(val channelId: String) : UpdatesIntent()
    data class ViewChannel(val channelId: String) : UpdatesIntent()
    object Refresh : UpdatesIntent()
}
