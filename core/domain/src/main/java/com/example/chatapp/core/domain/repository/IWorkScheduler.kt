package com.example.chatapp.core.domain.repository

interface IWorkScheduler {
    fun scheduleMessageSend(
        messageId: String,
        text: String?,
        mediaUris: List<String>?,
        senderId: String,
        senderName: String,
        timestamp: Long
    )
}
