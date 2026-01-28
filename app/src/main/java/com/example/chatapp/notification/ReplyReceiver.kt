package com.example.chatapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import com.example.chatapp.core.domain.usecase.GetDeviceIdUseCase
import com.example.chatapp.core.domain.usecase.GetUsernameUseCase
import com.example.chatapp.core.domain.usecase.SendMessageUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * BroadcastReceiver that handles:
 * - Inline reply from notification
 * - Mark as read action
 */
@AndroidEntryPoint
class ReplyReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var sendMessageUseCase: SendMessageUseCase
    
    @Inject
    lateinit var getUsernameUseCase: GetUsernameUseCase
    
    @Inject
    lateinit var getDeviceIdUseCase: GetDeviceIdUseCase
    
    @Inject
    lateinit var notificationManager: ChatNotificationManager
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ChatNotificationManager.ACTION_REPLY -> handleReply(intent)
            ChatNotificationManager.ACTION_MARK_READ -> handleMarkRead(intent)
        }
    }
    
    /**
     * Handles inline reply from notification.
     */
    private fun handleReply(intent: Intent) {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        val replyText = remoteInput?.getCharSequence(ChatNotificationManager.KEY_TEXT_REPLY)?.toString()
        val senderId = intent.getStringExtra(ChatNotificationManager.KEY_SENDER_ID)
        
        if (replyText.isNullOrBlank() || senderId == null) return
        
        scope.launch {
            try {
                // Get current user info
                val currentUserId = getDeviceIdUseCase().first()
                val currentUserName = getUsernameUseCase().first() ?: "Anonymous"
                
                // Send the reply message
                sendMessageUseCase(
                    text = replyText,
                    mediaUris = null,
                    senderId = currentUserId,
                    senderName = currentUserName
                )
                
                // Clear notification for this sender
                notificationManager.clearNotificationsForSender(senderId)
                
            } catch (e: Exception) {
                // Log error or show error notification
            }
        }
    }
    
    /**
     * Handles mark as read action.
     */
    private fun handleMarkRead(intent: Intent) {
        val senderId = intent.getStringExtra(ChatNotificationManager.KEY_SENDER_ID)
        
        if (senderId != null) {
            notificationManager.clearNotificationsForSender(senderId)
        }
    }
}
