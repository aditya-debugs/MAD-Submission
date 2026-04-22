package com.example.madass2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Boot Receiver - Restart background tasks after device reboot
 *
 * This receiver ensures that periodic background tasks are automatically
 * rescheduled when the device boots up.
 *
 * Note: This receiver is disabled by default to prevent unnecessary work.
 * Enable it in AndroidManifest.xml if you want automatic task rescheduling
 * on device boot.
 */
class BootReceiver : BroadcastReceiver() {

    private val TAG = "BootReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device boot completed, rescheduling background tasks")

            context?.let {
                // Reschedule periodic tasks
                schedulePeriodicTasks(it)
            }
        }
    }

    /**
     * Reschedule periodic background tasks
     */
    private fun schedulePeriodicTasks(context: Context) {
        try {
            val workRequest = PeriodicWorkRequestBuilder<DataFetchWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueue(workRequest)
            Log.d(TAG, "Periodic tasks rescheduled successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reschedule tasks: ${e.message}", e)
        }
    }
}

