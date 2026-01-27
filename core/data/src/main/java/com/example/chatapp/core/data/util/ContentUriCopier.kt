package com.example.chatapp.core.data.util

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentUriCopier @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Copies the content from the given content URI to a temporary file in the app's cache directory.
     * Returns the URI of the successfully created local file, or null if the operation failed.
     */
    suspend fun copyToInternalStorage(uri: Uri): Uri? = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            // Try to get extension, default to 'bin' or 'tmp' if unknown.
            val mimeType = contentResolver.getType(uri)
            val extension = if (mimeType != null && mimeType.contains("/")) {
                mimeType.substringAfter("/")
            } else {
                "bin"
            }
                
            val fileName = "upload_media_${UUID.randomUUID()}.$extension"
            val file = File(context.cacheDir, fileName)
            
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
