package com.example.chatapp.feature.chat_room.presentation.model

import com.example.chatapp.core.ui.mvi.UiEffect

sealed class SearchEffect : UiEffect {
    data class ScrollToMessage(val messageId: String) : SearchEffect()
    data class ShowError(val message: String) : SearchEffect()
}
