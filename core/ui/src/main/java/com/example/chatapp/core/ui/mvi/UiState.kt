package com.example.chatapp.core.ui.mvi

/**
 * Marker interface for UI State in MVI architecture.
 * 
 * UI State represents the complete state of a screen at any given moment.
 * It should be:
 * - Immutable (use data class)
 * - Contain all data needed to render the UI
 * - Have sensible default values
 * 
 * Example:
 * ```
 * data class HomeState(
 *     val items: List<Item> = emptyList(),
 *     val isLoading: Boolean = false,
 *     val error: String? = null
 * ) : UiState
 * ```
 */
interface UiState
