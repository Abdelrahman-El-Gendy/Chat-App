package com.example.chatapp.feature.chat_room.presentation.model

import com.example.chatapp.core.ui.mvi.UiIntent

sealed class CallsIntent : UiIntent {
    object LoadCalls : CallsIntent()
    data class CallBack(val callId: String) : CallsIntent()
    data class StartVideoCall(val userId: String) : CallsIntent()
    data class StartAudioCall(val userId: String) : CallsIntent()
    object ViewAllMissed : CallsIntent()
    object Refresh : CallsIntent()
}
