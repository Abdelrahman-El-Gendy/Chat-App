package com.example.chatapp.feature.chat_room.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_rendersInLightTheme() {
        composeTestRule.setContent {
            MaterialTheme {
                EmptyState(title = "No Messages")
            }
        }
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun emptyState_rendersInDarkTheme() {
        composeTestRule.setContent {
            MaterialTheme {
                EmptyState(title = "No Messages")
            }
        }
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun shimmerLoading_rendersInBothThemes() {
        composeTestRule.setContent {
            MaterialTheme {
                ShimmerLoading(width = 100f, height = 20f)
            }
        }
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun messageSkeletonLoader_rendersInBothThemes() {
        composeTestRule.setContent {
            MaterialTheme {
                MessageSkeletonLoader()
            }
        }
        composeTestRule.onRoot().assertExists()
    }
}
