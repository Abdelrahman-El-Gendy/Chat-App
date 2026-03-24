package com.example.chatapp.core.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Elevation levels for the Terra design system.
 * Terra philosophy: "Very soft shadows only. Prefer tonal separation over shadows."
 * All shadow elevations are intentionally low – never harsh or deep.
 */
object Elevation {
    /** No elevation – flat surfaces that rely solely on tonal colour separation. */
    val Level0 = 0.dp

    /** Cards, message bubbles – barely perceptible lift. */
    val Level1 = 1.dp

    /** Dialogs, input surfaces – soft, present but not dramatic. */
    val Level2 = 2.dp

    /** Bottom sheets, app bars – still very gentle. */
    val Level3 = 4.dp

    /** Snackbars, menus, tooltips – the highest shadow in the system. */
    val Level4 = 6.dp
}
