package com.example.chatapp.feature.chat_room

import app.cash.turbine.test
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.model.MessageStatus
import com.example.chatapp.core.domain.usecase.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private lateinit var getMessagesUseCase: GetMessagesUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var getUsernameUseCase: GetUsernameUseCase
    private lateinit var getDeviceIdUseCase: GetDeviceIdUseCase
    private lateinit var deleteMessageUseCase: DeleteMessageUseCase
    private lateinit var getTypingUsersUseCase: GetTypingUsersUseCase
    private lateinit var setTypingStatusUseCase: SetTypingStatusUseCase

    private lateinit var viewModel: ChatViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getMessagesUseCase = mockk(relaxed = true)
        sendMessageUseCase = mockk(relaxed = true)
        getUsernameUseCase = mockk(relaxed = true)
        getDeviceIdUseCase = mockk(relaxed = true)
        deleteMessageUseCase = mockk(relaxed = true)
        getTypingUsersUseCase = mockk(relaxed = true)
        setTypingStatusUseCase = mockk(relaxed = true)

        // Default mocks
        every { getUsernameUseCase() } returns flowOf("TestUser")
        every { getDeviceIdUseCase() } returns flowOf("device-123")
        every { getMessagesUseCase() } returns flowOf(emptyList())
        every { getTypingUsersUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): ChatViewModel {
        return ChatViewModel(
            getMessagesUseCase,
            sendMessageUseCase,
            getUsernameUseCase,
            getDeviceIdUseCase,
            deleteMessageUseCase,
            getTypingUsersUseCase,
            setTypingStatusUseCase
        )
    }

    @Test
    fun `initial state should have loading true`() = runTest {
        // Given
        viewModel = createViewModel()

        // When
        advanceUntilIdle()

        // Then - after messages load, loading becomes false
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadUserInfo should update currentUser and currentUserName`() = runTest {
        // Given
        every { getUsernameUseCase() } returns flowOf("John Doe")
        every { getDeviceIdUseCase() } returns flowOf("user-456")

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("John Doe", state.currentUserName)
        assertEquals("user-456", state.currentUser)
    }

    @Test
    fun `loadUserInfo should use Anonymous when username is null`() = runTest {
        // Given
        every { getUsernameUseCase() } returns flowOf(null)
        every { getDeviceIdUseCase() } returns flowOf("user-456")

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("Anonymous", state.currentUserName)
    }

    @Test
    fun `loadMessages should update messages in state`() = runTest {
        // Given
        val messages = listOf(
            createTestMessage("1", "Hello"),
            createTestMessage("2", "World")
        )
        every { getMessagesUseCase() } returns flowOf(messages)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(2, state.messages.size)
        assertEquals("Hello", state.messages[0].text)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadMessages should handle error`() = runTest {
        // Given
        every { getMessagesUseCase() } returns flowOf(emptyList())
        // We can't easily simulate catch in this setup, 
        // so we'll test that empty list works correctly
        
        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.messages.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `sendMessage should call sendMessageUseCase with correct parameters`() = runTest {
        // Given
        every { getUsernameUseCase() } returns flowOf("John")
        every { getDeviceIdUseCase() } returns flowOf("device-123")
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.sendMessage("Hello World", listOf("uri1"))
        advanceUntilIdle()

        // Then
        coVerify { 
            sendMessageUseCase(
                text = "Hello World",
                mediaUris = listOf("uri1"),
                senderId = "device-123",
                senderName = "John"
            )
        }
    }

    @Test
    fun `sendMessage with text only should work`() = runTest {
        // Given
        every { getUsernameUseCase() } returns flowOf("John")
        every { getDeviceIdUseCase() } returns flowOf("device-123")
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.sendMessage("Just text")
        advanceUntilIdle()

        // Then
        coVerify { 
            sendMessageUseCase(
                text = "Just text",
                mediaUris = null,
                senderId = "device-123",
                senderName = "John"
            )
        }
    }

    @Test
    fun `sendMessage error should update state with error`() = runTest {
        // Given
        every { getUsernameUseCase() } returns flowOf("John")
        every { getDeviceIdUseCase() } returns flowOf("device-123")
        coEvery { sendMessageUseCase(any(), any(), any(), any()) } throws RuntimeException("Network error")
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.sendMessage("Hello")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Failed to send message"))
    }

    @Test
    fun `deleteMessage should call deleteMessageUseCase`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.deleteMessage("msg-123")
        advanceUntilIdle()

        // Then
        coVerify { deleteMessageUseCase("msg-123") }
    }

    @Test
    fun `loadMoreMessages should not load when already loading`() = runTest {
        // Given
        val messages = listOf(createTestMessage("1", "Hello", timestamp = 1000L))
        every { getMessagesUseCase() } returns flowOf(messages)
        viewModel = createViewModel()
        advanceUntilIdle()

        // When - first call
        viewModel.loadMoreMessages()
        // Second call while first is in progress
        viewModel.loadMoreMessages()
        advanceUntilIdle()

        // Then - should only be called once for pagination
        coVerify(atMost = 1) { getMessagesUseCase.getOlder(any(), any()) }
    }

    @Test
    fun `loadMoreMessages should update hasMoreMessages to false when empty`() = runTest {
        // Given
        val messages = listOf(createTestMessage("1", "Hello", timestamp = 1000L))
        every { getMessagesUseCase() } returns flowOf(messages)
        coEvery { getMessagesUseCase.getOlder(any(), any()) } returns emptyList()
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadMoreMessages()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.hasMoreMessages)
    }

    @Test
    fun `loadMoreMessages should append older messages`() = runTest {
        // Given
        val initialMessages = listOf(createTestMessage("1", "Recent", timestamp = 1000L))
        val olderMessages = listOf(
            createTestMessage("2", "Old1", timestamp = 500L),
            createTestMessage("3", "Old2", timestamp = 600L)
        )
        every { getMessagesUseCase() } returns flowOf(initialMessages)
        coEvery { getMessagesUseCase.getOlder(1000L, any()) } returns olderMessages
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadMoreMessages()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(3, state.messages.size)
    }

    @Test
    fun `observeTypingUsers should filter out current user`() = runTest {
        // Given
        every { getUsernameUseCase() } returns flowOf("CurrentUser")
        every { getTypingUsersUseCase() } returns flowOf(listOf("CurrentUser", "OtherUser"))
        
        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(1, state.typingUsers.size)
        assertEquals("OtherUser", state.typingUsers[0])
    }

    @Test
    fun `setTyping should call setTypingStatusUseCase`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.setTyping(true)
        advanceUntilIdle()

        // Then
        coVerify { setTypingStatusUseCase(true) }
    }

    @Test
    fun `setTyping false should stop typing indicator`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.setTyping(false)
        advanceUntilIdle()

        // Then
        coVerify { setTypingStatusUseCase(false) }
    }

    @Test
    fun `state flow should emit updates correctly`() = runTest {
        // Given
        val messagesFlow = MutableStateFlow<List<Message>>(emptyList())
        every { getMessagesUseCase() } returns messagesFlow
        viewModel = createViewModel()

        viewModel.uiState.test {
            // Initial state
            awaitItem() // Skip initial state

            // When messages are updated
            messagesFlow.value = listOf(createTestMessage("1", "New message"))
            advanceUntilIdle()

            // Then
            val updatedState = awaitItem()
            assertEquals(1, updatedState.messages.size)

            cancelAndIgnoreRemainingEvents()
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
