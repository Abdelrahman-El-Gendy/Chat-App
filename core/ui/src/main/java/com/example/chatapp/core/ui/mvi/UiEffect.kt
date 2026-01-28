package com.example.chatapp.core.ui.mvi

/**
 * Marker interface for Side Effects in MVI architecture.
 * 
 * Effects represent one-time events that should not be part of the state.
 * They are consumed once and not re-emitted on configuration changes.
 * 
 * Use cases:
 * - Navigation events
 * - Showing snackbars/toasts
 * - Opening dialogs
 * - Playing sounds
 * - Triggering haptic feedback
 * 
 * Example:
 * ```
 * sealed class HomeEffect : UiEffect {
 *     data class NavigateToDetail(val itemId: String) : HomeEffect()
 *     data class ShowError(val message: String) : HomeEffect()
 *     object ShowSuccessToast : HomeEffect()
 * }
 * ```
 */
interface UiEffect
