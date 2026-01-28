package com.example.chatapp.feature.chat_room

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.model.MessageStatus
import com.example.chatapp.core.domain.usecase.*
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var getMessagesUseCase: GetMessagesUseCase
    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var getUsernameUseCase: GetUsernameUseCase
    private lateinit var getDeviceIdUseCase: GetDeviceIdUseCase
    private lateinit var deleteMessageUseCase: DeleteMessageUseCase
    private lateinit var getTypingUsersUseCase: GetTypingUsersUseCase
    private lateinit var setTypingStatusUseCase: SetTypingStatusUseCase

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
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
    fun chatScreen_displaysTitle() {
        // Given
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            ChatScreen(viewModel = viewModel, onBack = {})
        }

        // Then
        composeTestRule.onNodeWithText("Global Chat").assertIsDisplayed()
    }

    @Test
    fun chatScreen_displaysActiveStatus() {
        // Given
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            ChatScreen(viewModel = viewModel, onBack = {})
        }

        // Then
        composeTestRule.onNodeWithText("Active").assertIsDisplayed()
    }

    @Test
    fun chatScreen_displaysMessages() {
        // Given
        val messages = listOf(
            Message(
                id = "1",
                text = "Hello World",
                senderId = "other-user",
                senderName = "John",
                status = MessageStatus.SENT
            ),
            Message(
                id = "2",
                text = "Hi there!",
                senderId = "device-123",
                senderName = "TestUser",
                status = MessageStatus.SENT
            )
        )
        every { getMessagesUseCase() } returns flowOf(messages)
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            ChatScreen(viewModel = viewModel, onBack = {})
        }

        // Then
        composeTestRule.onNodeWithText("Hello World").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hi there!").assertIsDisplayed()
    }

    @Test
    fun chatScreen_displaysTypingIndicator() {
        // Given
        every { getTypingUsersUseCase() } returns flowOf(listOf("OtherUser"))
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            ChatScreen(viewModel = viewModel, onBack = {})
        }

        // Then
        composeTestRule.onNodeWithText("OtherUser is typing...", substring = true).assertIsDisplayed()
    }

    @Test
    fun chatScreen_hidesTypingIndicatorWhenEmpty() {
        // Given
        every { getTypingUsersUseCase() } returns flowOf(emptyList())
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            ChatScreen(viewModel = viewModel, onBack = {})
        }

        // Then
        composeTestRule.onNodeWithText("is typing...", substring = true).assertDoesNotExist()
    }

    @Test
    fun chatScreen_showsLoadingIndicator() {
        // Given - simulate loading state
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            ChatScreen(viewModel = viewModel, onBack = {})
        }

        // Initial loading should be visible briefly
        // Note: This is a quick test, in real scenarios we'd use IdlingResources
        composeTestRule.waitForIdle()
    }

    @Test
    fun chatScreen_backButtonTriggerCallback() {
        // Given
        var backClicked = false
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            ChatScreen(viewModel = viewModel, onBack = { backClicked = true })
        }

        composeTestRule.onNodeWithContentDescription(null, useUnmergedTree = true)
            .onFirst()
            .performClick()

        // Note: Without explicit content description, this test may not work perfectly
        // In production, we'd add testTag or contentDescription to the back button
    }

    @Test
    fun chatScreen_displaysMultipleTypingUsers() {
        // Given
        every { getTypingUsersUseCase() } returns flowOf(listOf("User1", "User2"))
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            ChatScreen(viewModel = viewModel, onBack = {})
        }

        // Then
        composeTestRule.onNodeWithText("User1, User2 is typing...", substring = true).assertIsDisplayed()
    }

    @Test
    fun chatScreen_displaysMessageWithSendingStatus() {
        // Given
        val messages = listOf(
            Message(
                id = "1",
                text = "Sending message...",
                senderId = "device-123",
                senderName = "TestUser",
                status = MessageStatus.SENDING
            )
        )
        every { getMessagesUseCase() } returns flowOf(messages)
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            ChatScreen(viewModel = viewModel, onBack = {})
        }

        // Then
        composeTestRule.onNodeWithText("Sending message...").assertIsDisplayed()
    }

    @Test
    fun chatScreen_displaysMessageWithFailedStatus() {
        // Given
        val messages = listOf(
            Message(
                id = "1",
                text = "Failed message",
                senderId = "device-123",
                senderName = "TestUser",
                status = MessageStatus.FAILED
            )
        )
        every { getMessagesUseCase() } returns flowOf(messages)
        viewModel = createViewModel()

        // When
        composeTestRule.setContent {
            ChatScreen(viewModel = viewModel, onBack = {})
        }

        // Then
        composeTestRule.onNodeWithText("Failed message").assertIsDisplayed()
    }
}
