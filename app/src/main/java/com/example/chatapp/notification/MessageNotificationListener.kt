package com.example.chatapp.notification

import android.app.ActivityManager
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.usecase.GetDeviceIdUseCase
import com.example.chatapp.core.domain.usecase.GetMessagesUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Listens for new messages from Firebase and shows notifications
 * when the app is in the background.
 * 
 * Uses ProcessLifecycleOwner to detect app foreground/background state.
 */
@Singleton
class MessageNotificationListener @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val getDeviceIdUseCase: GetDeviceIdUseCase,
    private val notificationManager: ChatNotificationManager
) : DefaultLifecycleObserver {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isAppInForeground = true
    private var lastKnownMessageTimestamp = 0L
    private var currentUserId: String? = null
    
    /**
     * Starts listening for new messages.
     * Should be called from Application.onCreate()
     */
    fun startListening() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        
        scope.launch {
            // Get current user ID
            currentUserId = getDeviceIdUseCase().first()
            
            // Start observing messages
            observeMessages()
        }
    }
    
    override fun onStart(owner: LifecycleOwner) {
        // App came to foreground
        isAppInForeground = true
        // Clear notifications when user opens the app
        notificationManager.clearAllNotifications()
    }
    
    override fun onStop(owner: LifecycleOwner) {
        // App went to background
        isAppInForeground = false
        // Update last known timestamp to avoid showing old messages
        lastKnownMessageTimestamp = System.currentTimeMillis()
    }
    
    /**
     * Observes messages from Firebase and shows notifications for new messages.
     */
    private fun observeMessages() {
        scope.launch {
            getMessagesUseCase().collect { messages ->
                if (!isAppInForeground) {
                    // Filter new messages from other users
                    val newMessages = messages.filter { message ->
                        message.timestamp > lastKnownMessageTimestamp &&
                        message.senderId != currentUserId
                    }
                    
                    // Show notification for each new message
                    newMessages.forEach { message ->
                        showNotificationForMessage(message)
                    }
                    
                    // Update last known timestamp
                    messages.maxByOrNull { it.timestamp }?.let {
                        lastKnownMessageTimestamp = it.timestamp
                    }
                }
            }
        }
    }
    
    /**
     * Shows a notification for a specific message.
     */
    private fun showNotificationForMessage(message: Message) {
        val messageText = message.text ?: buildMediaMessageText(message)
        
        notificationManager.showMessageNotification(
            messageId = message.id,
            senderId = message.senderId,
            senderName = message.senderName,
            messageText = messageText,
            timestamp = message.timestamp
        )
    }
    
    /**
     * Builds a text description for media-only messages.
     */
    private fun buildMediaMessageText(message: Message): String {
        val mediaCount = message.mediaUrls?.size ?: 0
        return when {
            mediaCount == 1 -> "Sent a photo"
            mediaCount > 1 -> "Sent $mediaCount photos"
            else -> "Sent a message"
        }
    }
}
