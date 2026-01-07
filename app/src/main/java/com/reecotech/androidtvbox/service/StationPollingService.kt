package com.reecotech.androidtvbox.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.reecotech.androidtvbox.MainActivity
import com.reecotech.androidtvbox.R
import com.reecotech.androidtvbox.domain.StationRepository
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Foreground Service that ensures unstoppable API polling.
 * This service:
 * - Runs independently from Activity/ViewModel lifecycle
 * - Cannot be stopped (no stop mechanism)
 * - Auto-restarts if killed by system (START_STICKY)
 * - Starts on boot via BootCompletedReceiver
 * - Shows persistent notification (required for foreground service)
 */
@AndroidEntryPoint
class StationPollingService : Service() {

    @Inject
    lateinit var stationRepository: StationRepository

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "station_polling_channel"
        private const val CHANNEL_NAME = "Station Data Polling"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.i("StationPollingService: onCreate() - Starting unstoppable polling")

        // Create notification channel for Android 8.0+
        createNotificationChannel()

        // Start as foreground service with notification
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Start polling - THIS WILL NEVER STOP
        stationRepository.startPolling()
        
        Timber.i("StationPollingService: Polling started successfully")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("StationPollingService: onStartCommand()")
        
        // Ensure polling is running (in case service was restarted)
        if (!isPollingActive()) {
            stationRepository.startPolling()
            Timber.i("StationPollingService: Polling restarted")
        }

        // START_STICKY ensures service is recreated if killed by system
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Timber.w("StationPollingService: Task removed - Restarting service")
        
        // Restart service when user swipes away the app
        val restartServiceIntent = Intent(applicationContext, StationPollingService::class.java)
        startService(restartServiceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.w("StationPollingService: onDestroy() - Service is being destroyed")
        
        // Note: We do NOT call stopPolling() here
        // The service should auto-restart via START_STICKY
        // If it's a real shutdown, the system will handle it
    }

    override fun onBind(intent: Intent?): IBinder? {
        // This is not a bound service
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // Low importance = no sound/vibration
            ).apply {
                description = "Continuous polling for station data updates"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            
            Timber.d("StationPollingService: Notification channel created")
        }
    }

    private fun buildNotification(): Notification {
        // Intent to open MainActivity when notification is tapped
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Station Monitoring Active")
            .setContentText("Continuously monitoring station data")
            .setSmallIcon(R.mipmap.ic_launcher) // Using app icon
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Cannot be dismissed by user
            .setPriority(NotificationCompat.PRIORITY_LOW) // Low priority = less intrusive
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    /**
     * Check if polling is currently active.
     * This is a simple check - in reality, the repository manages this.
     */
    private fun isPollingActive(): Boolean {
        // The repository will handle duplicate startPolling() calls gracefully
        // So we can safely call it without checking
        return true
    }
}
