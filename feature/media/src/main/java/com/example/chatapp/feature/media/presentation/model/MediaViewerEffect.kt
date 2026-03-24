package com.example.chatapp.feature.media.presentation.model

import com.example.chatapp.core.ui.mvi.UiEffect

sealed class MediaViewerEffect : UiEffect {
    data class ShareFile(val url: String) : MediaViewerEffect()
    data class SaveToGallery(val url: String) : MediaViewerEffect()
    object Close : MediaViewerEffect()
    data class ShowError(val message: String) : MediaViewerEffect()
}
