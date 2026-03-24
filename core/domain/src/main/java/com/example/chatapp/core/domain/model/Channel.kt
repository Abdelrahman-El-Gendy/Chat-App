package com.example.chatapp.core.domain.model

data class Channel(
    val id: String,
    val name: String,
    val description: String = "",
    val lastMessage: String? = null,
    val lastMessageTimestamp: Long = 0L,
    val unreadCount: Int = 0,
    val memberCount: Int = 0
)
