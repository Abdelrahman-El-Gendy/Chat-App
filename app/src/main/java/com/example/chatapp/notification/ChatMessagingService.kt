package com.example.chatapp.notification

import android.app.ActivityManager
import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Firebase Cloud Messaging service that handles:
 * - Incoming push notifications when app is in background
 * - FCM token refresh
 */
@AndroidEntryPoint
class ChatMessagingService : FirebaseMessagingService() {
    
    @Inject
    lateinit var notificationManager: ChatNotificationManager
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Only show notification if app is in background
        if (!isAppInForeground()) {
            handleDataMessage(remoteMessage.data)
        }
    }
    
    /**
     * Handles data payload from FCM message.
     */
    private fun handleDataMessage(data: Map<String, String>) {
        val messageId = data["messageId"] ?: return
        val senderId = data["senderId"] ?: return
        val senderName = data["senderName"] ?: "Unknown"
        val messageText = data["messageText"] ?: ""
        val timestamp = data["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis()
        
        notificationManager.showMessageNotification(
            messageId = messageId,
            senderId = senderId,
            senderName = senderName,
            messageText = messageText,
            timestamp = timestamp
        )
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to server for push notification targeting
        // This would typically be saved to Firebase Realtime Database
        // associated with the user's device ID
    }
    
    /**
     * Checks if the app is currently in the foreground.
     */
    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && appProcess.processName == packageName
            ) {
                return true
            }
        }
        return false
    }
}
