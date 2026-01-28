package com.example.chatapp.feature.work

import android.content.Context
import androidx.work.*
import com.example.chatapp.core.domain.repository.IWorkScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WorkSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : IWorkScheduler {

    override fun scheduleMessageSend(
        messageId: String,
        text: String?,
        mediaUris: List<String>?,
        senderId: String,
        senderName: String,
        timestamp: Long
    ) {
        val workManager = WorkManager.getInstance(context)
        
        if (!mediaUris.isNullOrEmpty()) {
            val uploadWork = OneTimeWorkRequestBuilder<UploadMediaWorker>()
                .setInputData(workDataOf("media_uris" to mediaUris.toTypedArray()))
                .build()
                
            val sendWork = OneTimeWorkRequestBuilder<SendMessageWorker>()
                .setInputData(workDataOf(
                    "message_id" to messageId,
                    "text" to text,
                    "sender_id" to senderId,
                    "sender_name" to senderName,
                    "timestamp" to timestamp
                ))
                .build()
                
            workManager.beginWith(uploadWork).then(sendWork).enqueue()
        } else {
            val sendWork = OneTimeWorkRequestBuilder<SendMessageWorker>()
                .setInputData(workDataOf(
                    "message_id" to messageId,
                    "text" to text,
                    "sender_id" to senderId,
                    "sender_name" to senderName,
                    "timestamp" to timestamp
                ))
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            workManager.enqueue(sendWork)
        }
    }
}
