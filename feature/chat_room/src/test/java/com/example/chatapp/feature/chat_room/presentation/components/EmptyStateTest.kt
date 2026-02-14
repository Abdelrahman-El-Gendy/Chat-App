package com.example.chatapp.feature.chat_room.presentation.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmptyStateTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_displaysTitle() {
        val testTitle = "Test Title"
        composeTestRule.setContent {
            EmptyState(title = testTitle)
        }
        composeTestRule.onNodeWithText(testTitle).assertExists()
    }

    @Test
    fun emptyState_displaysDescription() {
        val testDescription = "Test Description"
        composeTestRule.setContent {
            EmptyState(description = testDescription)
        }
        composeTestRule.onNodeWithText(testDescription).assertExists()
    }

    @Test
    fun emptyState_displaysActionButton_whenProvided() {
        val actionLabel = "Action"
        composeTestRule.setContent {
            EmptyState(actionLabel = actionLabel, onAction = {})
        }
        composeTestRule.onNodeWithText(actionLabel).assertExists()
    }

    @Test
    fun emptyState_hidesActionButton_whenNotProvided() {
        composeTestRule.setContent {
            EmptyState(actionLabel = null, onAction = null)
        }
        // Should not find any button text
        composeTestRule.onNodeWithText("Action").assertDoesNotExist()
    }
}
