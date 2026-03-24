package com.example.chatapp.feature.chat_room.presentation.model

import com.example.chatapp.core.ui.mvi.UiEffect

sealed class CallsEffect : UiEffect {
    data class ShowError(val message: String) : CallsEffect()
    data class NavigateToCall(val callId: String, val isVideo: Boolean) : CallsEffect()
}
