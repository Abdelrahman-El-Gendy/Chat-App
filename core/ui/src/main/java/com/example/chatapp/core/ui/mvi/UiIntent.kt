package com.example.chatapp.core.ui.mvi

/**
 * Marker interface for User Intents in MVI architecture.
 * 
 * Intents represent user actions or events that can change the state.
 * They should be:
 * - Immutable
 * - Sealed class/interface for type safety
 * - Descriptive of the user action
 * 
 * Example:
 * ```
 * sealed class HomeIntent : UiIntent {
 *     object LoadData : HomeIntent()
 *     object RefreshData : HomeIntent()
 *     data class ItemClicked(val itemId: String) : HomeIntent()
 *     data class SearchQueryChanged(val query: String) : HomeIntent()
 * }
 * ```
 */
interface UiIntent
