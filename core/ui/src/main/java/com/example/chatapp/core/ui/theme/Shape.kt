package com.example.chatapp.core.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Shape definitions for the Terra design system.
 * Roundness: ROUND_TWELVE  (12 dp as the baseline corner radius).
 * Principle: Avoid sharp corners – everything should feel soft and approachable.
 */
object Shape {
    /**
     * Extra small – minimal rounding for small buttons, tags, chips.
     */
    val extraSmall = 4.dp

    /**
     * Small – media previews, avatar clips, small card decorations.
     */
    val small = 8.dp

    /**
     * Medium – the primary Terra radius: message bubbles, cards, dialogs.
     * ROUND_TWELVE baseline per Stitch design theme.
     */
    val medium = 12.dp

    /**
     * Large – input fields, bottom sheets, prominent panels.
     */
    val large = 16.dp

    /**
     * Extra large – large cards, expanded panels.
     */
    val extraLarge = 24.dp

    /**
     * Maximum – pill-shaped chips, FABs, circular elements.
     */
    val maximum = 9999.dp
}
