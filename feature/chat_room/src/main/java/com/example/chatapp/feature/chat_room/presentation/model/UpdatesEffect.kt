package com.example.chatapp.feature.chat_room.presentation.model

import com.example.chatapp.core.ui.mvi.UiEffect

sealed class UpdatesEffect : UiEffect {
    data class ShowError(val message: String) : UpdatesEffect()
    data class NavigateToStory(val storyId: String) : UpdatesEffect()
    data class NavigateToChannel(val channelId: String) : UpdatesEffect()
    object OpenCamera : UpdatesEffect()
}
