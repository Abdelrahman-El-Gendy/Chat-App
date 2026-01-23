package com.example.chatapp.core.domain.model

data class User(
    val id: String,
    val username: String,
    val profilePictureUrl: String? = null,
    val isOnline: Boolean = false
)
