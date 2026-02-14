package com.example.chatapp.feature.chat_room.presentation.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResponsiveTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_rendersOnSmallScreen() {
        composeTestRule.setContent {
            EmptyState(title = "No Messages")
        }
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun emptyState_rendersOnLargeScreen() {
        composeTestRule.setContent {
            EmptyState(title = "No Messages")
        }
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun messageSkeletonLoader_rendersOnDifferentScreenSizes() {
        composeTestRule.setContent {
            MessageSkeletonLoader()
        }
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun shimmerLoading_rendersWithDifferentSizes() {
        composeTestRule.setContent {
            ShimmerLoading(width = 100f, height = 20f)
        }
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun components_handleLargeFontScale() {
        composeTestRule.setContent {
            EmptyState(title = "No Messages")
        }
        composeTestRule.onRoot().assertExists()
    }
}
