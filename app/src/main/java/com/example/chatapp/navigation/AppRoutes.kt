package com.example.chatapp.navigation

/**
 * Defines all navigation routes in the app.
 *
 * Uses string-based routes compatible with Navigation Compose 2.7.x.
 */
object AppRoutes {
    const val USERNAME = "username"
    const val CHANNEL_LIST = "channel_list"
    const val CHAT = "chat/{channelId}"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val SEARCH = "search"
    const val MEDIA_VIEWER = "media_viewer/{startIndex}"

    // Builder helpers
    fun chat(channelId: String) = "chat/$channelId"
    fun mediaViewer(startIndex: Int) = "media_viewer/$startIndex"
}
