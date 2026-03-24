package com.example.chatapp.feature.media.presentation.model

import androidx.compose.runtime.Immutable
import com.example.chatapp.core.ui.mvi.UiState

@Immutable
data class MediaViewerState(
    val mediaUrls: List<String> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false,
    val isZoomed: Boolean = false
) : UiState {
    val currentUrl: String?
        get() = mediaUrls.getOrNull(currentIndex)
    
    val isVideo: Boolean
        get() = currentUrl?.let {
            it.contains("video", ignoreCase = true) ||
            it.endsWith(".mp4") || it.endsWith(".webm") || it.endsWith(".mov")
        } ?: false

    val pageIndicator: String
        get() = if (mediaUrls.isNotEmpty()) "${currentIndex + 1} / ${mediaUrls.size}" else ""
}
