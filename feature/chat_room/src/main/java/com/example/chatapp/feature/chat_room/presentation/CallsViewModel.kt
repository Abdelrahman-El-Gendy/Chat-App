package com.example.chatapp.feature.chat_room.presentation

import com.example.chatapp.core.ui.mvi.BaseMviViewModel
import com.example.chatapp.feature.chat_room.presentation.model.CallItem
import com.example.chatapp.feature.chat_room.presentation.model.CallType
import com.example.chatapp.feature.chat_room.presentation.model.CallsEffect
import com.example.chatapp.feature.chat_room.presentation.model.CallsIntent
import com.example.chatapp.feature.chat_room.presentation.model.CallsState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CallsViewModel @Inject constructor() : BaseMviViewModel<CallsState, CallsIntent, CallsEffect>(
    initialState = CallsState()
) {
    init {
        loadSampleCalls()
    }

    override suspend fun handleIntent(intent: CallsIntent) {
        when (intent) {
            is CallsIntent.LoadCalls -> loadSampleCalls()
            is CallsIntent.CallBack -> setEffect(CallsEffect.NavigateToCall(intent.callId, false))
            is CallsIntent.StartVideoCall -> setEffect(CallsEffect.NavigateToCall(intent.userId, true))
            is CallsIntent.StartAudioCall -> setEffect(CallsEffect.NavigateToCall(intent.userId, false))
            is CallsIntent.ViewAllMissed -> { /* TODO: filter missed */ }
            is CallsIntent.Refresh -> loadSampleCalls()
        }
    }

    private fun loadSampleCalls() {
        val sampleCalls = listOf(
            CallItem("1", "Willow Gardener", null, System.currentTimeMillis() - 7200000, CallType.OUTGOING, false, "Last call: 2 hours ago"),
            CallItem("2", "Julian Thorne", null, System.currentTimeMillis() - 43200000, CallType.INCOMING, true, "% Mobile • 12:45 AM"),
            CallItem("3", "Elena Moss", null, System.currentTimeMillis() - 86400000, CallType.INCOMING, false, "% Mobile • 10:22 AM"),
            CallItem("4", "Fern Brooks", null, System.currentTimeMillis() - 172800000, CallType.MISSED, false, "% Mobile • 8:30 PM"),
            CallItem("5", "Cedar Wood", null, System.currentTimeMillis() - 259200000, CallType.OUTGOING, true, "% Mobile • 2:15 PM"),
        )
        setState { copy(recentCalls = sampleCalls, missedCallsCount = 3, isLoading = false) }
    }
}
