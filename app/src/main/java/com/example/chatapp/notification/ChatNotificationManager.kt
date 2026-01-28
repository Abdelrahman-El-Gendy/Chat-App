package com.example.chatapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages chat notifications including:
 * - Creating notification channels
 * - Showing message notifications with reply action
 * - Updating badge count
 * - Grouping multiple messages
 */
@Singleton
class ChatNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "chat_messages"
        const val CHANNEL_NAME = "Chat Messages"
        const val CHANNEL_DESCRIPTION = "Notifications for new chat messages"
        
        const val GROUP_KEY = "com.example.chatapp.MESSAGES"
        const val SUMMARY_ID = 0
        
        const val KEY_TEXT_REPLY = "key_text_reply"
        const val KEY_MESSAGE_ID = "key_message_id"
        const val KEY_SENDER_ID = "key_sender_id"
        const val KEY_SENDER_NAME = "key_sender_name"
        
        const val ACTION_REPLY = "com.example.chatapp.ACTION_REPLY"
        const val ACTION_MARK_READ = "com.example.chatapp.ACTION_MARK_READ"
        
        private const val BADGE_SIZE = 128
    }
    
    private val notificationManager = NotificationManagerCompat.from(context)
    private val pendingMessages = mutableMapOf<String, MutableList<MessageData>>()
    
    init {
        createNotificationChannel()
    }
    
    /**
     * Creates the notification channel for Android O and above.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
                setShowBadge(true)
            }
            
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Shows a notification for a new message with reply action.
     * 
     * @param messageId Unique ID of the message
     * @param senderId ID of the sender
     * @param senderName Name of the sender
     * @param messageText Content of the message
     * @param timestamp When the message was sent
     */
    fun showMessageNotification(
        messageId: String,
        senderId: String,
        senderName: String,
        messageText: String,
        timestamp: Long
    ) {
        // Store message for grouping
        val messageData = MessageData(messageId, senderId, senderName, messageText, timestamp)
        pendingMessages.getOrPut(senderId) { mutableListOf() }.add(messageData)
        
        // Create unique notification ID based on sender
        val notificationId = senderId.hashCode()
        
        // Build the notification
        val notification = buildMessageNotification(
            notificationId = notificationId,
            senderId = senderId,
            senderName = senderName,
            messages = pendingMessages[senderId] ?: listOf(messageData)
        )
        
        // Show notification
        try {
            notificationManager.notify(notificationId, notification.build())
            
            // Show summary notification for grouping
            if (getTotalPendingCount() > 1) {
                showSummaryNotification()
            }
            
            // Update app badge
            updateBadgeCount(getTotalPendingCount())
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
    
    /**
     * Builds a notification with messaging style and reply action.
     */
    private fun buildMessageNotification(
        notificationId: Int,
        senderId: String,
        senderName: String,
        messages: List<MessageData>
    ): NotificationCompat.Builder {
        // Create person for messaging style
        val person = Person.Builder()
            .setName(senderName)
            .setIcon(createAvatarIcon(senderName))
            .build()
        
        // Create messaging style
        val messagingStyle = NotificationCompat.MessagingStyle(person)
            .setConversationTitle(senderName)
        
        // Add all messages
        messages.forEach { message ->
            messagingStyle.addMessage(
                message.text,
                message.timestamp,
                person
            )
        }
        
        // Create intent to open app
        val contentIntent = createContentIntent(senderId)
        
        // Create reply action
        val replyAction = createReplyAction(notificationId, senderId, senderName)
        
        // Create mark as read action
        val markReadAction = createMarkReadAction(notificationId, senderId)
        
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setStyle(messagingStyle)
            .setContentIntent(contentIntent)
            .addAction(replyAction)
            .addAction(markReadAction)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setNumber(messages.size)
            .setLargeIcon(createAvatarBitmap(senderName))
    }
    
    /**
     * Creates a reply action with RemoteInput for inline reply.
     */
    private fun createReplyAction(
        notificationId: Int,
        senderId: String,
        senderName: String
    ): NotificationCompat.Action {
        val replyLabel = context.getString(R.string.notification_reply)
        
        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel(replyLabel)
            .build()
        
        val replyIntent = Intent(context, ReplyReceiver::class.java).apply {
            action = ACTION_REPLY
            putExtra(KEY_SENDER_ID, senderId)
            putExtra(KEY_SENDER_NAME, senderName)
            putExtra(KEY_MESSAGE_ID, notificationId)
        }
        
        val replyPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        
        return NotificationCompat.Action.Builder(
            R.drawable.ic_reply,
            replyLabel,
            replyPendingIntent
        )
            .addRemoteInput(remoteInput)
            .setAllowGeneratedReplies(true)
            .build()
    }
    
    /**
     * Creates a mark as read action.
     */
    private fun createMarkReadAction(
        notificationId: Int,
        senderId: String
    ): NotificationCompat.Action {
        val markReadIntent = Intent(context, ReplyReceiver::class.java).apply {
            action = ACTION_MARK_READ
            putExtra(KEY_SENDER_ID, senderId)
            putExtra(KEY_MESSAGE_ID, notificationId)
        }
        
        val markReadPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 1000, // Offset to avoid conflict
            markReadIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Action.Builder(
            R.drawable.ic_mark_read,
            context.getString(R.string.notification_mark_read),
            markReadPendingIntent
        ).build()
    }
    
    /**
     * Creates an intent to open the app when notification is clicked.
     */
    private fun createContentIntent(senderId: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(KEY_SENDER_ID, senderId)
        }
        
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    /**
     * Shows a summary notification for message grouping.
     */
    private fun showSummaryNotification() {
        val totalCount = getTotalPendingCount()
        val senderCount = pendingMessages.size
        
        val summaryText = context.resources.getQuantityString(
            R.plurals.notification_summary,
            totalCount,
            totalCount,
            senderCount
        )
        
        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(context.getString(R.string.app_name))
            .setSummaryText(summaryText)
        
        // Add recent messages to inbox style
        pendingMessages.values.flatten()
            .sortedByDescending { it.timestamp }
            .take(5)
            .forEach { message ->
                inboxStyle.addLine("${message.senderName}: ${message.text}")
            }
        
        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setStyle(inboxStyle)
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .setAutoCancel(true)
            .setNumber(totalCount)
            .build()
        
        try {
            notificationManager.notify(SUMMARY_ID, summaryNotification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
    
    /**
     * Updates the app badge with the pending message count.
     */
    private fun updateBadgeCount(count: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Create a badge notification
            val badgeNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.resources.getQuantityString(
                    R.plurals.notification_badge,
                    count,
                    count
                ))
                .setNumber(count)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .build()
            
            // ShortcutBadger for older devices
            try {
                val badgeIntent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
                badgeIntent.putExtra("badge_count", count)
                badgeIntent.putExtra("badge_count_package_name", context.packageName)
                badgeIntent.putExtra("badge_count_class_name", MainActivity::class.java.name)
                context.sendBroadcast(badgeIntent)
            } catch (e: Exception) {
                // Badge not supported
            }
        }
    }
    
    /**
     * Clears notifications for a specific sender.
     */
    fun clearNotificationsForSender(senderId: String) {
        pendingMessages.remove(senderId)
        notificationManager.cancel(senderId.hashCode())
        
        // Update or remove summary
        if (pendingMessages.isEmpty()) {
            notificationManager.cancel(SUMMARY_ID)
            updateBadgeCount(0)
        } else {
            showSummaryNotification()
            updateBadgeCount(getTotalPendingCount())
        }
    }
    
    /**
     * Clears all chat notifications.
     */
    fun clearAllNotifications() {
        pendingMessages.clear()
        notificationManager.cancelAll()
        updateBadgeCount(0)
    }
    
    /**
     * Gets the total count of pending messages.
     */
    fun getTotalPendingCount(): Int = pendingMessages.values.sumOf { it.size }
    
    /**
     * Creates an avatar icon for the person in messaging style.
     */
    private fun createAvatarIcon(name: String): androidx.core.graphics.drawable.IconCompat {
        return androidx.core.graphics.drawable.IconCompat.createWithBitmap(
            createAvatarBitmap(name)
        )
    }
    
    /**
     * Creates a circular avatar bitmap with the first letter of the name.
     */
    private fun createAvatarBitmap(name: String): Bitmap {
        val bitmap = Bitmap.createBitmap(BADGE_SIZE, BADGE_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Background color based on name hash
        val colors = arrayOf(
            Color.parseColor("#F44336"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#673AB7"),
            Color.parseColor("#3F51B5"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#00BCD4"),
            Color.parseColor("#009688"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#FF9800")
        )
        val bgColor = colors[Math.abs(name.hashCode()) % colors.size]
        
        // Draw circle
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = bgColor
        canvas.drawCircle(
            BADGE_SIZE / 2f,
            BADGE_SIZE / 2f,
            BADGE_SIZE / 2f,
            paint
        )
        
        // Draw letter
        paint.color = Color.WHITE
        paint.textSize = BADGE_SIZE * 0.5f
        paint.textAlign = Paint.Align.CENTER
        
        val initial = name.firstOrNull()?.uppercaseChar() ?: '?'
        val textBounds = Rect()
        paint.getTextBounds(initial.toString(), 0, 1, textBounds)
        
        canvas.drawText(
            initial.toString(),
            BADGE_SIZE / 2f,
            BADGE_SIZE / 2f + textBounds.height() / 2f,
            paint
        )
        
        return bitmap
    }
    
    /**
     * Data class to hold message information for notifications.
     */
    data class MessageData(
        val messageId: String,
        val senderId: String,
        val senderName: String,
        val text: String,
        val timestamp: Long
    )
}
