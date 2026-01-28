package com.example.chatapp.core.data.repository

import android.net.Uri
import app.cash.turbine.test
import com.example.chatapp.core.data.remote.FirebaseMessageService
import com.example.chatapp.core.data.util.ContentUriCopier
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.model.MessageStatus
import com.example.chatapp.core.domain.repository.IWorkScheduler
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MessageRepositoryTest {

    private lateinit var firebaseService: FirebaseMessageService
    private lateinit var workScheduler: IWorkScheduler
    private lateinit var contentUriCopier: ContentUriCopier
    private lateinit var repository: MessageRepository

    @Before
    fun setUp() {
        firebaseService = mockk(relaxed = true)
        workScheduler = mockk(relaxed = true)
        contentUriCopier = mockk(relaxed = true)
        repository = MessageRepository(firebaseService, workScheduler, contentUriCopier)
    }

    @Test
    fun `getMessages should return flow from firebase service`() = runTest {
        // Given
        val messages = listOf(
            createTestMessage("1", "Hello"),
            createTestMessage("2", "World")
        )
        every { firebaseService.getMessages() } returns flowOf(messages)

        // When & Then
        repository.getMessages().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("Hello", result[0].text)
            awaitComplete()
        }
    }

    @Test
    fun `sendMessage should call firebase service`() = runTest {
        // Given
        val message = createTestMessage("1", "Test message")
        coEvery { firebaseService.sendMessage(message) } just runs

        // When
        repository.sendMessage(message)

        // Then
        coVerify { firebaseService.sendMessage(message) }
    }

    @Test
    fun `getOlderMessages should return messages from firebase service`() = runTest {
        // Given
        val lastTimestamp = 1000L
        val limit = 20
        val olderMessages = listOf(
            createTestMessage("1", "Old 1", timestamp = 500L),
            createTestMessage("2", "Old 2", timestamp = 600L)
        )
        coEvery { firebaseService.getOlderMessages(lastTimestamp, limit) } returns olderMessages

        // When
        val result = repository.getOlderMessages(lastTimestamp, limit)

        // Then
        assertEquals(2, result.size)
        assertEquals(500L, result[0].timestamp)
    }

    @Test
    fun `queueMessage with text only should schedule message without media`() = runTest {
        // Given
        val text = "Hello"
        val senderId = "user1"
        val senderName = "John"

        // When
        repository.queueMessage(text, null, senderId, senderName)

        // Then
        verify { firebaseService.sendMessageNonSuspend(match { 
            it.text == text && 
            it.senderId == senderId && 
            it.senderName == senderName &&
            it.status == MessageStatus.SENDING
        }) }
        verify { workScheduler.scheduleMessageSend(any(), text, null, senderId, senderName) }
    }

    @Test
    fun `queueMessage with media should copy URIs and schedule message`() = runTest {
        // Given
        val text = "With media"
        val mediaUris = listOf("content://media/1", "content://media/2")
        val senderId = "user1"
        val senderName = "John"
        val copiedUri = mockk<Uri>()
        every { copiedUri.toString() } returns "file://internal/cached"
        every { contentUriCopier.copyToInternalStorage(any()) } returns copiedUri

        // When
        repository.queueMessage(text, mediaUris, senderId, senderName)

        // Then
        verify(exactly = 2) { contentUriCopier.copyToInternalStorage(any()) }
        verify { workScheduler.scheduleMessageSend(
            any(), 
            text, 
            listOf("file://internal/cached", "file://internal/cached"), 
            senderId, 
            senderName
        ) }
    }

    @Test
    fun `queueMessage with empty media list should not copy URIs`() = runTest {
        // Given
        val text = "No media"
        val mediaUris = emptyList<String>()
        val senderId = "user1"
        val senderName = "John"

        // When
        repository.queueMessage(text, mediaUris, senderId, senderName)

        // Then
        verify(exactly = 0) { contentUriCopier.copyToInternalStorage(any()) }
    }

    @Test
    fun `queueMessage should handle failed copy gracefully`() = runTest {
        // Given
        val text = "With media"
        val mediaUris = listOf("content://media/1", "content://media/2")
        val senderId = "user1"
        val senderName = "John"
        every { contentUriCopier.copyToInternalStorage(any()) } returns null

        // When
        repository.queueMessage(text, mediaUris, senderId, senderName)

        // Then
        verify { workScheduler.scheduleMessageSend(
            any(), 
            text, 
            emptyList(), 
            senderId, 
            senderName
        ) }
    }

    @Test
    fun `deleteMessage should call firebase service`() = runTest {
        // Given
        val messageId = "msg-123"
        coEvery { firebaseService.deleteMessage(messageId) } just runs

        // When
        repository.deleteMessage(messageId)

        // Then
        coVerify { firebaseService.deleteMessage(messageId) }
    }

    @Test
    fun `getTypingUsers should return flow from firebase service`() = runTest {
        // Given
        val users = listOf("User1", "User2")
        every { firebaseService.getTypingUsers() } returns flowOf(users)

        // When & Then
        repository.getTypingUsers().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertTrue(result.contains("User1"))
            awaitComplete()
        }
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
