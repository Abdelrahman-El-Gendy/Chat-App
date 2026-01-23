package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.repository.IMessageRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class SendMessageUseCaseTest {

    private val repository = mockk<IMessageRepository>(relaxed = true)
    private val useCase = SendMessageUseCase(repository)

    @Test
    fun `invoke should call queueMessage on repository`() {
        val text = "Hello"
        val mediaUris = listOf("uri1")
        val senderId = "user1"
        val senderName = "John"

        useCase(text, mediaUris, senderId, senderName)

        verify { repository.queueMessage(text, mediaUris, senderId, senderName) }
    }
}
