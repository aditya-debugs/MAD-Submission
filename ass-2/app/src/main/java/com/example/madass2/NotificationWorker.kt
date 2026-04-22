package com.example.madass2

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Legacy Notification Worker
 * Now uses centralized NotificationHelper for consistency
 */
class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val TAG = "NotificationWorker"

    override fun doWork(): Result {
        return try {
            Log.d(TAG, "Notification worker started")

            // Create notification channels
            NotificationHelper.createNotificationChannels(applicationContext)

            // Simulate background task: e.g., fetch data
            Thread.sleep(2000)

            // Get current timestamp
            val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            // Show notification
            NotificationHelper.showBackgroundTaskNotification(
                applicationContext,
                "Background Task Completed",
                "Data fetched successfully at $timestamp"
            )

            Log.d(TAG, "Notification sent successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}", e)
            Result.retry()
        }
    }
}
