package com.example.chatapp.feature.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ForegroundInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.chatapp.core.domain.model.Message
import com.example.chatapp.core.domain.model.MessageStatus
import com.example.chatapp.core.domain.repository.IMessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class SendMessageWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: IMessageRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val messageId = inputData.getString("message_id") ?: return Result.failure()
        val text = inputData.getString("text")
        val senderId = inputData.getString("sender_id") ?: return Result.failure()
        val senderName = inputData.getString("sender_name") ?: return Result.failure()
        val timestamp = inputData.getLong("timestamp", System.currentTimeMillis())
        
        // Receive from previous worker if chained (uploaded_urls), or from direct input (media_urls)
        val mediaUrls = inputData.getStringArray("uploaded_urls")?.toList() 
            ?: inputData.getStringArray("media_urls")?.toList()

        try {
            setForeground(createForegroundInfo())
        } catch (e: Exception) {
            // Foreground service may fail on some devices, continue anyway
            android.util.Log.w("SendMessageWorker", "Failed to set foreground: ${e.message}")
        }

        return try {
            val message = Message(
                id = messageId,
                text = text,
                mediaUrls = mediaUrls,
                senderId = senderId,
                senderName = senderName,
                timestamp = timestamp,
                status = MessageStatus.SENT
            )
            repository.sendMessage(message)
            android.util.Log.d("SendMessageWorker", "Message sent successfully: $messageId")
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("SendMessageWorker", "Failed to send message: ${e.message}")
            if (runAttemptCount >= 3) {
                try {
                    repository.sendMessage(Message(
                        id = messageId,
                        text = text,
                        mediaUrls = mediaUrls,
                        senderId = senderId,
                        senderName = senderName,
                        timestamp = timestamp,
                        status = MessageStatus.FAILED
                    ))
                } catch (updateError: Exception) {
                    android.util.Log.e("SendMessageWorker", "Failed to update status to FAILED: ${updateError.message}")
                }
                Result.failure()
            } else {
                Result.retry()
            }
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val channelId = "chat_notifications"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Chat Notifications",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = androidx.core.app.NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Sending message...")
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setOngoing(true)
            .build()

        return ForegroundInfo(1, notification)
    }
}
