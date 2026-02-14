package com.example.chatapp.core.ui.theme

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.CubicBezierEasing

/**
 * Animation constants following Material3 guidelines.
 *
 * Duration values are defined in milliseconds (ms).
 * Easing curves follow Material3 specification for entrances and exits.
 */
object Animation {

    /**
     * Standard animation durations
     */
    object Duration {
        /**
         * Micro-interactions: short feedback animations
         * Example: button presses, toggles
         */
        val MicroInteraction = 150

        /**
         * Transitions: screen and component transitions
         * Example: navigation transitions, dialog appearances
         */
        val Transition = 300

        /**
         * Complex animations: multi-step or detailed animations
         * Example: onboarding sequences, complex state changes
         */
        val Complex = 500

        /**
         * Message appearance animation
         * Slightly longer than transition for readability
         */
        val MessageAppearance = 300

        /**
         * Media preview expand/collapse animation
         * Balanced duration for smooth expansion
         */
        val MediaPreviewToggle = 250
    }

    /**
     * Easing curves for animations
     *
     * Material3 recommends:
     * - ease-out (FastOutLinearIn) for entrances
     * - ease-in (LinearOutSlowIn) for exits
     */
    object Easing {
        /**
         * Ease-out curve for entrances
         * Starts fast, slows down gradually
         */
        val Entrance = FastOutLinearInEasing

        /**
         * Ease-in curve for exits
         * Starts fast, slows down gradually
         * Note: Material3 uses this for both entrances and exits
         */
        val Exit = LinearOutSlowInEasing

        /**
         * Standard easing for general animations
         * Balanced curve for smooth motion
         */
        val Standard = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

        /**
         * Sharp easing for more pronounced animations
         */
        val Sharp = CubicBezierEasing(0.55f, 0.0f, 0.1f, 1.0f)
    }
}
