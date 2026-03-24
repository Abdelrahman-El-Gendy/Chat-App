package com.example.chatapp.feature.chat_room.presentation.model

import com.example.chatapp.core.ui.mvi.UiIntent

sealed class SearchIntent : UiIntent {
    data class UpdateQuery(val query: String) : SearchIntent()
    object ClearSearch : SearchIntent()
    data class ScrollToMessage(val messageId: String) : SearchIntent()
}
