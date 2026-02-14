package com.example.chatapp.core.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Shape definitions for the design system.
 * Provides consistent corner radius values throughout the application.
 */
object Shape {
    /**
     * Small corner radius for subtle rounding.
     * Used for: Media previews, small chips
     */
    val small = 12.dp

    /**
     * Medium corner radius for standard components.
     * Used for: Message bubbles (asymmetric), cards
     */
    val medium = 20.dp

    /**
     * Large corner radius for prominent components.
     * Used for: Input fields, dialogs, bottom sheets
     */
    val large = 24.dp

    /**
     * Extra small corner radius for minimal rounding.
     * Used for: Small buttons, tags
     */
    val extraSmall = 4.dp

    /**
     * Extra large corner radius for maximum rounding.
     * Used for: Large cards, panels
     */
    val extraLarge = 16.dp

    /**
     * Maximum corner radius for pill-shaped elements.
     * Used for: Chips, pills, circular elements
     */
    val maximum = 9999.dp
}
