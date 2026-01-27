package com.example.chatapp.feature.work

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ForegroundInfo
import androidx.work.workDataOf
import com.google.firebase.storage.FirebaseStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.util.UUID

@HiltWorker
class UploadMediaWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val storage: FirebaseStorage
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val mediaUriStrings = inputData.getStringArray("media_uris") ?: return Result.failure()
        val uploadUrls = mutableListOf<String>()

        setForeground(createForegroundInfo(0, mediaUriStrings.size))

        return try {
            mediaUriStrings.forEachIndexed { index, uriString ->
                val uri = Uri.parse(uriString)
                if (uri.scheme == "http" || uri.scheme == "https") {
                    uploadUrls.add(uriString)
                } else {
                    val ref = storage.reference.child("media/${UUID.randomUUID()}")
                    ref.putFile(uri).await()
                    val downloadUrl = ref.downloadUrl.await().toString()
                    uploadUrls.add(downloadUrl)
                }
                setForeground(createForegroundInfo(index + 1, mediaUriStrings.size))
            }
            
            Result.success(workDataOf("uploaded_urls" to uploadUrls.toTypedArray()))
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun createForegroundInfo(current: Int, total: Int): ForegroundInfo {
        val channelId = "upload_notifications"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Media Uploads",
                android.app.NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = androidx.core.app.NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Uploading media...")
            .setContentText("Uploading $current of $total")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setProgress(total, current, false)
            .setOngoing(true)
            .build()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            return ForegroundInfo(
                2,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        }
        return ForegroundInfo(2, notification)
    }
}
