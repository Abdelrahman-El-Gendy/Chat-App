package com.example.chatapp.feature.chat_room.presentation.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnimationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_animatesIn() {
        composeTestRule.setContent {
            EmptyState(title = "Test")
        }
        // Verify the empty state is visible after animation
        composeTestRule.onNodeWithText("Test").assertExists()
    }

    @Test
    fun shimmerLoading_renders() {
        composeTestRule.setContent {
            ShimmerLoading(width = 100f, height = 20f)
        }
        // Verify shimmer component renders without error
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun messageSkeletonLoader_renders() {
        composeTestRule.setContent {
            MessageSkeletonLoader()
        }
        // Verify skeleton loader renders without error
        composeTestRule.onRoot().assertExists()
    }
}
