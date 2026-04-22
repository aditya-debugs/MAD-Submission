package com.example.madass2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Main Activity - Background Tasks & Notifications Demo
 *
 * This activity demonstrates:
 * 1. Periodic background tasks using WorkManager
 * 2. Foreground services for continuous tasks
 * 3. Notification channels and delivery
 * 4. Permission handling for Android 13+
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    // Permission request launcher
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
            Toast.makeText(this, R.string.msg_permission_granted, Toast.LENGTH_SHORT).show()
        } else {
            Log.d(TAG, "Notification permission denied")
            Toast.makeText(this, R.string.msg_permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    private val requestForegroundServicePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Foreground service permission granted")
            startDataFetchService()
        } else {
            Toast.makeText(this, R.string.msg_permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Apply edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize notification channels
        NotificationHelper.createNotificationChannels(this)

        // Request notification permission if needed
        requestNotificationPermission()

        // Setup UI
        setupUI()
    }

    /**
     * Request POST_NOTIFICATIONS permission for Android 13+
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Requesting POST_NOTIFICATIONS permission")
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    /**
     * Request FOREGROUND_SERVICE permission for Android 12+
     */
    private fun requestForegroundServicePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.FOREGROUND_SERVICE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Requesting FOREGROUND_SERVICE permission")
                requestForegroundServicePermissionLauncher.launch(Manifest.permission.FOREGROUND_SERVICE)
            } else {
                startDataFetchService()
            }
        } else {
            startDataFetchService()
        }
    }

    /**
     * Setup UI components
     */
    private fun setupUI() {
        val mainContainer = findViewById<LinearLayout>(R.id.main)

        // Clear default content
        mainContainer.removeAllViews()

        // Create scroll view for better UX with many buttons
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val containerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(20, 20, 20, 20)
        }

        // Title
        val titleView = TextView(this).apply {
            text = getString(R.string.title_background_tasks)
            textSize = 24f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        }
        containerLayout.addView(titleView)

        // Subtitle
        val subtitleView = TextView(this).apply {
            text = getString(R.string.subtitle_tasks)
            textSize = 14f
            setPadding(0, 0, 0, 24)
        }
        containerLayout.addView(subtitleView)

        // Section: WorkManager Tasks
        val workManagerLabel = TextView(this).apply {
            text = "WorkManager - Periodic Tasks"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }
        containerLayout.addView(workManagerLabel)

        val btnPeriodicTask = createStyledButton(
            getString(R.string.btn_periodic_task),
            { schedulePeriodicBackgroundTask() }
        )
        containerLayout.addView(btnPeriodicTask)

        val btnOneTimeTask = createStyledButton(
            getString(R.string.btn_one_time_task),
            { scheduleOneTimeBackgroundTask() }
        )
        containerLayout.addView(btnOneTimeTask)

        // Spacer
        containerLayout.addView(createSpacer())

        // Section: Foreground Service
        val serviceLabel = TextView(this).apply {
            text = getString(R.string.label_foreground_service)
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }
        containerLayout.addView(serviceLabel)

        val btnStartService = createStyledButton(
            getString(R.string.btn_foreground_service),
            { requestForegroundServicePermission() }
        )
        containerLayout.addView(btnStartService)

        val btnStopService = createStyledButton(
            getString(R.string.btn_stop_service),
            { stopDataFetchService() }
        )
        containerLayout.addView(btnStopService)

        // Spacer
        containerLayout.addView(createSpacer())

        // Section: Manual Notifications
        val notificationLabel = TextView(this).apply {
            text = getString(R.string.label_notifications)
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }
        containerLayout.addView(notificationLabel)

        val btnShowDataSync = createStyledButton(
            "Show Data Sync Notification",
            { showDataSyncNotification() }
        )
        containerLayout.addView(btnShowDataSync)

        val btnShowReminder = createStyledButton(
            "Show Reminder Notification",
            { showReminderNotification() }
        )
        containerLayout.addView(btnShowReminder)

        scrollView.addView(containerLayout)
        mainContainer.addView(scrollView)
    }

    /**
     * Create a styled button
     */
    private fun createStyledButton(text: String, onClickListener: () -> Unit): Button {
        return Button(this).apply {
            setText(text)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
            setOnClickListener { onClickListener() }
        }
    }

    /**
     * Create a spacer view
     */
    private fun createSpacer(): android.view.View {
        return android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                40
            )
        }
    }

    /**
     * Schedule periodic background task using WorkManager
     *
     * Benefits of WorkManager:
     * - Automatic rescheduling after device reboot
     * - Battery optimizations
     * - Intelligent scheduling
     */
    private fun schedulePeriodicBackgroundTask() {
        Log.d(TAG, "Scheduling periodic background task")

        val workRequest = PeriodicWorkRequestBuilder<DataFetchWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueue(workRequest)
        Toast.makeText(this, R.string.msg_task_scheduled, Toast.LENGTH_SHORT).show()
    }

    /**
     * Schedule a one-time background task using WorkManager
     * Useful for immediate tasks that should not be periodic
     */
    private fun scheduleOneTimeBackgroundTask() {
        Log.d(TAG, "Scheduling one-time background task")

        val oneTimeWorkRequest = androidx.work.OneTimeWorkRequestBuilder<DataFetchWorker>()
            .build()

        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest)
        Toast.makeText(this, "One-time task scheduled", Toast.LENGTH_SHORT).show()
    }

    /**
     * Start foreground service for continuous background operations
     *
     * Key differences from background service:
     * - Shows persistent notification
     * - Less likely to be killed by system
     * - Requires notification for user awareness
     * - Limited to Android 8.0+ capabilities
     */
    private fun startDataFetchService() {
        Log.d(TAG, "Starting data fetch service")

        val serviceIntent = Intent(this, DataFetchService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        Toast.makeText(this, R.string.msg_service_started, Toast.LENGTH_SHORT).show()
    }

    /**
     * Stop foreground service
     */
    private fun stopDataFetchService() {
        Log.d(TAG, "Stopping data fetch service")

        val serviceIntent = Intent(this, DataFetchService::class.java)
        stopService(serviceIntent)

        Toast.makeText(this, R.string.msg_service_stopped, Toast.LENGTH_SHORT).show()
    }

    /**
     * Manually show data sync notification
     * Demonstrates how to show notifications from any part of the app
     */
    private fun showDataSyncNotification() {
        NotificationHelper.showBackgroundTaskNotification(
            this,
            getString(R.string.notification_data_sync),
            "All data synchronized successfully at ${System.currentTimeMillis()}"
        )
        Log.d(TAG, "Data sync notification shown")
    }

    /**
     * Manually show reminder notification
     * Demonstrates high-priority notification for important reminders
     */
    private fun showReminderNotification() {
        NotificationHelper.showReminderNotification(
            this,
            getString(R.string.notification_reminder),
            "Don't forget to check your pending tasks!"
        )
        Log.d(TAG, "Reminder notification shown")
    }
}

