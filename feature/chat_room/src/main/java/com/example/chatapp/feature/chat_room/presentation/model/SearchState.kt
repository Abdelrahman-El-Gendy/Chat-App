package com.example.chatapp.feature.chat_room.presentation.model

import androidx.compose.runtime.Immutable
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.ui.mvi.UiState

@Immutable
data class SearchState(
    val query: String = "",
    val results: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
) : UiState
