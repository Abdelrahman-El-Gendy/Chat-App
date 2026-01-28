package com.example.chatapp.core.domain.usecase

import app.cash.turbine.test
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.model.MessageStatus
import com.example.chatapp.core.domain.repository.IMessageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetMessagesUseCaseTest {

    private lateinit var repository: IMessageRepository
    private lateinit var useCase: GetMessagesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetMessagesUseCase(repository)
    }

    @Test
    fun `invoke should return messages flow from repository`() = runTest {
        // Given
        val messages = listOf(
            createTestMessage("1", "Hello"),
            createTestMessage("2", "World")
        )
        every { repository.getMessages() } returns flowOf(messages)

        // When & Then
        useCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Hello", result[0].text)
            assertEquals("World", result[1].text)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return empty list when no messages`() = runTest {
        // Given
        every { repository.getMessages() } returns flowOf(emptyList())

        // When & Then
        useCase().test {
            val result = awaitItem()
            assertEquals(0, result.size)
            awaitComplete()
        }
    }

    @Test
    fun `getOlder should return older messages from repository`() = runTest {
        // Given
        val lastTimestamp = 1000L
        val limit = 20
        val olderMessages = listOf(
            createTestMessage("1", "Old message 1", timestamp = 500L),
            createTestMessage("2", "Old message 2", timestamp = 600L)
        )
        coEvery { repository.getOlderMessages(lastTimestamp, limit) } returns olderMessages

        // When
        val result = useCase.getOlder(lastTimestamp, limit)

        // Then
        assertEquals(2, result.size)
        assertEquals(500L, result[0].timestamp)
        coVerify { repository.getOlderMessages(lastTimestamp, limit) }
    }

    @Test
    fun `getOlder with default limit should use 20`() = runTest {
        // Given
        val lastTimestamp = 1000L
        coEvery { repository.getOlderMessages(lastTimestamp, 20) } returns emptyList()

        // When
        useCase.getOlder(lastTimestamp)

        // Then
        coVerify { repository.getOlderMessages(lastTimestamp, 20) }
    }

    @Test
    fun `getOlder should return empty list when no older messages`() = runTest {
        // Given
        val lastTimestamp = 100L
        coEvery { repository.getOlderMessages(lastTimestamp, 20) } returns emptyList()

        // When
        val result = useCase.getOlder(lastTimestamp)

        // Then
        assertEquals(0, result.size)
    }

    private fun createTestMessage(
        id: String,
        text: String,
        timestamp: Long = System.currentTimeMillis()
    ) = Message(
        id = id,
        text = text,
        senderId = "user1",
        senderName = "Test User",
        timestamp = timestamp,
        status = MessageStatus.SENT
    )
}
