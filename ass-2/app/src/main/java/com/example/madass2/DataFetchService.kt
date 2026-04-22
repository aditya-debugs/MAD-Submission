package com.example.madass2

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import kotlin.concurrent.thread

/**
 * Foreground Service for Continuous Background Tasks
 *
 * This service runs in the foreground with a persistent notification.
 * It performs continuous background operations like data synchronization.
 *
 * Key Features:
 * - Runs with a persistent notification
 * - Can execute long-running tasks
 * - User can see it's running in the notification bar
 * - Survives system memory pressure better than background services
 */
class DataFetchService : Service() {

    private val TAG = "DataFetchService"
    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")

        // Create notification channels
        NotificationHelper.createNotificationChannels(this)

        // Show foreground notification
        val notification = NotificationHelper.showForegroundServiceNotification(
            this,
            getString(R.string.notification_foreground_service),
            "Fetching data in background..."
        )

        if (Build.VERSION.SDK_INT >= 34) { // Android 14+ (UPSIDE_DOWN_CAKE)
            @Suppress("NewApi")
            startForeground(NotificationHelper.NOTIFICATION_ID_FOREGROUND, notification, 2) // 2 = DATA_SYNC
        } else {
            startForeground(NotificationHelper.NOTIFICATION_ID_FOREGROUND, notification)
        }

        isRunning = true

        // Perform background task in a separate thread
        performBackgroundTask()

        return START_STICKY
    }

    /**
     * Simulate continuous background task (e.g., data fetching)
     */
    private fun performBackgroundTask() {
        thread {
            try {
                var taskCount = 0
                while (isRunning) {
                    // Simulate data fetch operation
                    Thread.sleep(10000) // 10 seconds interval for demo

                    taskCount++
                    Log.d(TAG, "Background task executed: $taskCount")

                    // Show notification for each completed task
                    NotificationHelper.showBackgroundTaskNotification(
                        this,
                        "Data Sync Task #$taskCount",
                        "Successfully synced data at ${System.currentTimeMillis()}"
                    )
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "Task interrupted: ${e.message}")
                Thread.currentThread().interrupt()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        isRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

