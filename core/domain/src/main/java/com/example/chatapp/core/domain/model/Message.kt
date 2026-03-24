package com.example.chatapp.core.domain.model

import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val text: String? = null,
    val mediaUrls: List<String>? = null,
    val senderId: String,
    val senderName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENDING,
    val channelId: String = "general"
)

enum class MessageStatus {
    SENDING, SENT, FAILED, READ
}

