package com.example.chatapp.core.data.remote.model

import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.model.MessageStatus
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class MessageDto(
    val id: String? = null,
    val text: String? = null,
    val mediaUrls: List<String>? = null,
    val senderId: String? = null,
    val senderName: String? = null,
    val timestamp: Long? = null,
    val status: String? = null
) {
    fun toDomain(): Message {
        return Message(
            id = id ?: "",
            text = text,
            mediaUrls = mediaUrls,
            senderId = senderId ?: "",
            senderName = senderName ?: "",
            timestamp = timestamp ?: 0L,
            status = MessageStatus.valueOf(status ?: MessageStatus.SENT.name)
        )
    }

    companion object {
        fun fromDomain(domain: Message): MessageDto {
            return MessageDto(
                id = domain.id,
                text = domain.text,
                mediaUrls = domain.mediaUrls,
                senderId = domain.senderId,
                senderName = domain.senderName,
                timestamp = domain.timestamp,
                status = domain.status.name
            )
        }
    }
}
