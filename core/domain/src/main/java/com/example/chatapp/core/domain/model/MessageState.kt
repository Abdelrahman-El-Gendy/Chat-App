package com.example.chatapp.core.domain.model

sealed class MessageState {
    object Idle : MessageState()
    object Loading : MessageState()
    data class Success(val messages: List<Message>) : MessageState()
    data class Error(val message: String) : MessageState()
}
