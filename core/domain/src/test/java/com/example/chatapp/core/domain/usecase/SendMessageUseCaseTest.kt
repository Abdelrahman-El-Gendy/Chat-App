package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.model.MessageStatus
import com.example.chatapp.core.domain.repository.IMessageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SendMessageUseCaseTest {

    private lateinit var repository: IMessageRepository
    private lateinit var useCase: SendMessageUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = SendMessageUseCase(repository)
    }

    @Test
    fun `invoke should call queueMessage on repository`() = runTest {
        // Given
        val text = "Hello"
        val mediaUris = listOf("uri1")
        val senderId = "user1"
        val senderName = "John"

        // When
        useCase(text, mediaUris, senderId, senderName)

        // Then
        coVerify { repository.queueMessage(text, mediaUris, senderId, senderName) }
    }

    @Test
    fun `invoke with null text should still queue message`() = runTest {
        // Given
        val mediaUris = listOf("uri1", "uri2")
        val senderId = "user1"
        val senderName = "John"

        // When
        useCase(null, mediaUris, senderId, senderName)

        // Then
        coVerify { repository.queueMessage(null, mediaUris, senderId, senderName) }
    }

    @Test
    fun `invoke with null mediaUris should still queue message`() = runTest {
        // Given
        val text = "Hello World"
        val senderId = "user1"
        val senderName = "John"

        // When
        useCase(text, null, senderId, senderName)

        // Then
        coVerify { repository.queueMessage(text, null, senderId, senderName) }
    }

    @Test
    fun `sendDirectly should call sendMessage on repository`() = runTest {
        // Given
        val message = Message(
            id = "msg-123",
            text = "Direct message",
            senderId = "user1",
            senderName = "John",
            status = MessageStatus.SENT
        )

        // When
        useCase.sendDirectly(message)

        // Then
        coVerify { repository.sendMessage(message) }
    }

    @Test
    fun `invoke with empty mediaUris list should queue message`() = runTest {
        // Given
        val text = "Hello"
        val mediaUris = emptyList<String>()
        val senderId = "user1"
        val senderName = "John"

        // When
        useCase(text, mediaUris, senderId, senderName)

        // Then
        coVerify { repository.queueMessage(text, mediaUris, senderId, senderName) }
    }
}
