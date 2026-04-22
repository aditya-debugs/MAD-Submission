package com.example.madass2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * Centralized Notification Manager
 * Handles creation and display of notifications with proper channels
 */
object NotificationHelper {

    // Notification Channel IDs
    const val CHANNEL_BACKGROUND_TASKS = "background_channel"
    const val CHANNEL_FOREGROUND_SERVICE = "foreground_channel"
    const val CHANNEL_REMINDERS = "reminders_channel"

    // Notification IDs
    const val NOTIFICATION_ID_BACKGROUND = 1
    const val NOTIFICATION_ID_FOREGROUND = 2
    const val NOTIFICATION_ID_REMINDER = 3

    /**
     * Create notification channels for Android 8.0+
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Background Tasks Channel
            val backgroundChannel = NotificationChannel(
                CHANNEL_BACKGROUND_TASKS,
                context.getString(R.string.channel_background_tasks),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for periodic background tasks"
                enableLights(true)
                enableVibration(true)
            }

            // Foreground Service Channel
            val foregroundChannel = NotificationChannel(
                CHANNEL_FOREGROUND_SERVICE,
                context.getString(R.string.channel_foreground_service),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for foreground service"
                enableLights(false)
                enableVibration(false)
            }

            // Reminders Channel
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                context.getString(R.string.channel_reminders),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminder notifications"
                enableLights(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(backgroundChannel)
            notificationManager.createNotificationChannel(foregroundChannel)
            notificationManager.createNotificationChannel(remindersChannel)
        }
    }

    /**
     * Show a background task notification
     */
    fun showBackgroundTaskNotification(
        context: Context,
        title: String,
        message: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_BACKGROUND_TASKS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        notificationManager.notify(NOTIFICATION_ID_BACKGROUND, notification)
    }

    /**
     * Show a foreground service notification
     */
    fun showForegroundServiceNotification(
        context: Context,
        title: String,
        message: String
    ): android.app.Notification {
        val notification = NotificationCompat.Builder(context, CHANNEL_FOREGROUND_SERVICE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        return notification
    }

    /**
     * Show a reminder notification
     */
    fun showReminderNotification(
        context: Context,
        title: String,
        message: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        notificationManager.notify(NOTIFICATION_ID_REMINDER, notification)
    }

    /**
     * Cancel notification by ID
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}

