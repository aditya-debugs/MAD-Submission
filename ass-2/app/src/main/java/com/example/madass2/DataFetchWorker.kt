package com.example.madass2

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Background Worker for Periodic Tasks using WorkManager
 *
 * This worker handles periodic background tasks efficiently.
 * WorkManager automatically handles:
 * - Scheduling optimization
 * - Device battery considerations
 * - App update compatibility
 *
 * Use Cases:
 * - Periodic data sync
 * - Scheduled reminders
 * - Regular updates
 */
class DataFetchWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val TAG = "DataFetchWorker"

    override fun doWork(): Result {
        return try {
            Log.d(TAG, "Periodic task started")

            // Simulate background task: fetch data, perform operations, etc.
            val data = fetchData()
            Log.d(TAG, "Data fetched: $data")

            // Show notification about task completion
            showTaskNotification(data)

            // Return success
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Task failed: ${e.message}", e)
            // Retry on failure (WorkManager will retry based on backoff policy)
            Result.retry()
        }
    }

    /**
     * Simulate fetching data from a backend or local storage
     */
    private fun fetchData(): String {
        // Simulate API call or database query with delay
        Thread.sleep(2000)

        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val recordCount = (1..100).random()

        return "Fetched $recordCount records at $timestamp"
    }

    /**
     * Show notification when task completes
     */
    private fun showTaskNotification(data: String) {
        NotificationHelper.createNotificationChannels(applicationContext)
        NotificationHelper.showBackgroundTaskNotification(
            applicationContext,
            getString(R.string.notification_periodic_task),
            data
        )
    }

    private fun getString(resId: Int): String {
        return applicationContext.getString(resId)
    }
}

