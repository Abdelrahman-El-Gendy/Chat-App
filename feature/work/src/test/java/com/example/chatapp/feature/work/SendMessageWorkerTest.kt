package com.example.chatapp.feature.work

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.model.MessageStatus
import com.example.chatapp.core.domain.repository.IMessageRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SendMessageWorkerTest {

    private lateinit var context: Context
    private lateinit var workerParams: WorkerParameters
    private lateinit var repository: IMessageRepository
    private lateinit var worker: SendMessageWorker

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        workerParams = mockk(relaxed = true)
        repository = mockk(relaxed = true)
        
        // Mock notification service (needed for setForeground)
        val notificationManager = mockk<android.app.NotificationManager>(relaxed = true)
        every { context.getSystemService(Context.NOTIFICATION_SERVICE) } returns notificationManager
    }

    private fun createWorkerWithInputData(data: Data): SendMessageWorker {
        every { workerParams.inputData } returns data
        every { workerParams.runAttemptCount } returns 0
        return SendMessageWorker(context, workerParams, repository)
    }

    @Test
    fun `doWork should return failure when message_id is missing`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString("text", "Hello")
            .putString("sender_id", "user1")
            .putString("sender_name", "John")
            .build()
        val worker = createWorkerWithInputData(inputData)

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun `doWork should return failure when sender_id is missing`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString("message_id", "msg-123")
            .putString("text", "Hello")
            .putString("sender_name", "John")
            .build()
        val worker = createWorkerWithInputData(inputData)

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun `doWork should return failure when sender_name is missing`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString("message_id", "msg-123")
            .putString("text", "Hello")
            .putString("sender_id", "user1")
            .build()
        val worker = createWorkerWithInputData(inputData)

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun `doWork should send message with SENT status on success`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString("message_id", "msg-123")
            .putString("text", "Hello World")
            .putString("sender_id", "user1")
            .putString("sender_name", "John")
            .build()
        val worker = createWorkerWithInputData(inputData)
        coEvery { repository.sendMessage(any()) } just runs

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { 
            repository.sendMessage(match { 
                it.id == "msg-123" &&
                it.text == "Hello World" &&
                it.senderId == "user1" &&
                it.senderName == "John" &&
                it.status == MessageStatus.SENT
            })
        }
    }

    @Test
    fun `doWork should use uploaded_urls from chained worker`() = runTest {
        // Given
        val uploadedUrls = arrayOf("https://storage.com/img1.jpg", "https://storage.com/img2.jpg")
        val inputData = Data.Builder()
            .putString("message_id", "msg-123")
            .putString("text", "With media")
            .putString("sender_id", "user1")
            .putString("sender_name", "John")
            .putStringArray("uploaded_urls", uploadedUrls)
            .build()
        val worker = createWorkerWithInputData(inputData)
        coEvery { repository.sendMessage(any()) } just runs

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { 
            repository.sendMessage(match { 
                it.mediaUrls == uploadedUrls.toList()
            })
        }
    }

    @Test
    fun `doWork should fallback to media_urls when uploaded_urls is absent`() = runTest {
        // Given
        val mediaUrls = arrayOf("file://local/img1.jpg")
        val inputData = Data.Builder()
            .putString("message_id", "msg-123")
            .putString("text", "With local media")
            .putString("sender_id", "user1")
            .putString("sender_name", "John")
            .putStringArray("media_urls", mediaUrls)
            .build()
        val worker = createWorkerWithInputData(inputData)
        coEvery { repository.sendMessage(any()) } just runs

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { 
            repository.sendMessage(match { 
                it.mediaUrls == mediaUrls.toList()
            })
        }
    }

    @Test
    fun `doWork should retry on exception when attempts less than 3`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString("message_id", "msg-123")
            .putString("text", "Hello")
            .putString("sender_id", "user1")
            .putString("sender_name", "John")
            .build()
        every { workerParams.inputData } returns inputData
        every { workerParams.runAttemptCount } returns 1
        val worker = SendMessageWorker(context, workerParams, repository)
        coEvery { repository.sendMessage(any()) } throws RuntimeException("Network error")

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun `doWork should mark message as FAILED after 3 attempts`() = runTest {
        // Given
        val inputData = Data.Builder()
            .putString("message_id", "msg-123")
            .putString("text", "Hello")
            .putString("sender_id", "user1")
            .putString("sender_name", "John")
            .build()
        every { workerParams.inputData } returns inputData
        every { workerParams.runAttemptCount } returns 3
        val worker = SendMessageWorker(context, workerParams, repository)
        coEvery { repository.sendMessage(any()) } throws RuntimeException("Network error")

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.failure(), result)
        coVerify { 
            repository.sendMessage(match { 
                it.id == "msg-123" &&
                it.status == MessageStatus.FAILED
            })
        }
    }
}
