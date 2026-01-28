package com.example.chatapp.core.domain.usecase

import com.example.chatapp.core.domain.repository.IMessageRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteMessageUseCaseTest {

    private lateinit var repository: IMessageRepository
    private lateinit var useCase: DeleteMessageUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DeleteMessageUseCase(repository)
    }

    @Test
    fun `invoke should call deleteMessage on repository`() = runTest {
        // Given
        val messageId = "msg-123"

        // When
        useCase(messageId)

        // Then
        coVerify { repository.deleteMessage(messageId) }
    }

    @Test
    fun `invoke with different message id should call deleteMessage correctly`() = runTest {
        // Given
        val messageId = "another-message-456"

        // When
        useCase(messageId)

        // Then
        coVerify { repository.deleteMessage(messageId) }
    }
}
