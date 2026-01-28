package com.example.chatapp.feature.work

import android.content.Context
import android.net.Uri
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UploadMediaWorkerTest {

    private lateinit var context: Context
    private lateinit var workerParams: WorkerParameters
    private lateinit var storage: FirebaseStorage
    private lateinit var worker: UploadMediaWorker

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        workerParams = mockk(relaxed = true)
        storage = mockk(relaxed = true)
        
        // Mock notification service
        val notificationManager = mockk<android.app.NotificationManager>(relaxed = true)
        every { context.getSystemService(Context.NOTIFICATION_SERVICE) } returns notificationManager
    }

    private fun createWorkerWithInputData(data: Data): UploadMediaWorker {
        every { workerParams.inputData } returns data
        return UploadMediaWorker(context, workerParams, storage)
    }

    @Test
    fun `doWork should return failure when media_uris is missing`() = runTest {
        // Given
        val inputData = Data.Builder().build()
        val worker = createWorkerWithInputData(inputData)

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun `doWork should skip upload for http URLs`() = runTest {
        // Given
        val httpUrls = arrayOf("https://example.com/img1.jpg", "http://example.com/img2.jpg")
        val inputData = Data.Builder()
            .putStringArray("media_uris", httpUrls)
            .build()
        val worker = createWorkerWithInputData(inputData)

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is ListenableWorker.Result.Success)
        val outputData = (result as ListenableWorker.Result.Success).outputData
        val uploadedUrls = outputData.getStringArray("uploaded_urls")
        assertNotNull(uploadedUrls)
        assertEquals(2, uploadedUrls?.size)
        assertEquals("https://example.com/img1.jpg", uploadedUrls?.get(0))
        assertEquals("http://example.com/img2.jpg", uploadedUrls?.get(1))
        
        // Verify no upload was attempted
        verify(exactly = 0) { storage.reference }
    }

    @Test
    fun `doWork should upload local files to Firebase Storage`() = runTest {
        // Given
        val localUris = arrayOf("file:///storage/img1.jpg")
        val inputData = Data.Builder()
            .putStringArray("media_uris", localUris)
            .build()
        val worker = createWorkerWithInputData(inputData)
        
        // Mock Firebase Storage chain
        val storageRef = mockk<StorageReference>()
        val childRef = mockk<StorageReference>()
        val uploadTask = mockk<UploadTask>()
        val downloadUrlTask = mockk<Task<Uri>>()
        val downloadUri = mockk<Uri>()
        
        every { storage.reference } returns storageRef
        every { storageRef.child(any()) } returns childRef
        coEvery { childRef.putFile(any()).await() } returns mockk()
        every { downloadUri.toString() } returns "https://firebasestorage.com/uploaded.jpg"
        coEvery { childRef.downloadUrl.await() } returns downloadUri

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is ListenableWorker.Result.Success)
        val outputData = (result as ListenableWorker.Result.Success).outputData
        val uploadedUrls = outputData.getStringArray("uploaded_urls")
        assertNotNull(uploadedUrls)
        assertEquals(1, uploadedUrls?.size)
        assertEquals("https://firebasestorage.com/uploaded.jpg", uploadedUrls?.get(0))
    }

    @Test
    fun `doWork should retry on exception`() = runTest {
        // Given
        val localUris = arrayOf("content://media/img1.jpg")
        val inputData = Data.Builder()
            .putStringArray("media_uris", localUris)
            .build()
        val worker = createWorkerWithInputData(inputData)
        
        val storageRef = mockk<StorageReference>()
        val childRef = mockk<StorageReference>()
        every { storage.reference } returns storageRef
        every { storageRef.child(any()) } returns childRef
        coEvery { childRef.putFile(any()).await() } throws RuntimeException("Upload failed")

        // When
        val result = worker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun `doWork should handle mixed URLs and local files`() = runTest {
        // Given
        val mixedUris = arrayOf("https://example.com/existing.jpg", "file:///local/new.jpg")
        val inputData = Data.Builder()
            .putStringArray("media_uris", mixedUris)
            .build()
        val worker = createWorkerWithInputData(inputData)
        
        val storageRef = mockk<StorageReference>()
        val childRef = mockk<StorageReference>()
        val downloadUri = mockk<Uri>()
        
        every { storage.reference } returns storageRef
        every { storageRef.child(any()) } returns childRef
        coEvery { childRef.putFile(any()).await() } returns mockk()
        every { downloadUri.toString() } returns "https://firebasestorage.com/new.jpg"
        coEvery { childRef.downloadUrl.await() } returns downloadUri

        // When
        val result = worker.doWork()

        // Then
        assertTrue(result is ListenableWorker.Result.Success)
        val outputData = (result as ListenableWorker.Result.Success).outputData
        val uploadedUrls = outputData.getStringArray("uploaded_urls")
        assertNotNull(uploadedUrls)
        assertEquals(2, uploadedUrls?.size)
        assertEquals("https://example.com/existing.jpg", uploadedUrls?.get(0))
        assertEquals("https://firebasestorage.com/new.jpg", uploadedUrls?.get(1))
    }
}
