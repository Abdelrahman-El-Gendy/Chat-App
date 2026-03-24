package com.example.chatapp.feature.media.presentation.model

import com.example.chatapp.core.ui.mvi.UiIntent

sealed class MediaViewerIntent : UiIntent {
    data class LoadMedia(val urls: List<String>, val startIndex: Int = 0) : MediaViewerIntent()
    object Share : MediaViewerIntent()
    object Download : MediaViewerIntent()
    object Close : MediaViewerIntent()
    data class PageChanged(val index: Int) : MediaViewerIntent()
}
