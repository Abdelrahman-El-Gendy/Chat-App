package com.example.chatapp.feature.chat_room.presentation.model

import androidx.compose.runtime.Immutable
import com.example.chatapp.core.ui.mvi.UiState

enum class CallType { INCOMING, OUTGOING, MISSED }

data class CallItem(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val timestamp: Long,
    val callType: CallType,
    val isVideo: Boolean = false,
    val duration: String? = null // e.g. "2 hours ago", "5 min"
)

@Immutable
data class CallsState(
    val recentCalls: List<CallItem> = emptyList(),
    val missedCallsCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
) : UiState
