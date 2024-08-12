package com.dededev.pomodorotimer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dededev.pomodorotimer.R

class PomodoroService : Service() {
    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        getNotificationManager()
        when (intent?.getStringExtra(TIMER_ACTION)) {
            START -> startTimer()
        }
        return START_STICKY
    }

    private fun startTimer() {
        updateNotification()
    }

    private fun updateNotification() {
        notificationManager.notify(1,buildNotification())
    }

    private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Pomodoro",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.setSound(null, null)
            notificationChannel.setShowBadge(true)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setOngoing(true)
            .setContentTitle("Notification Set!")
            .setContentText("Ready")
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.baseline_access_time_24)
            .build()
    }

    /*
    * Service Action is to determine what function to be called from Fragment with startService()
    * Intent Extras is to be filled with value
    * Intent Action is use for Broadcast receiver */
    companion object {
        const val CHANNEL_ID = "Pomodoro_Notification"

        //Service ACTION
        const val START = "START"
        const val PAUSE = "PAUSE"
        const val RESET = "RESET"
        const val GET_STATUS = "GET_STATUS"

        // Intent Extras
        const val TIMER_ACTION = "TIMER_ACTION"
        const val TIME_LEFT = "TIME_LEFT"
        const val IS_TIMER_RUNNING = "IS_TIMER_RUNNING"

        // Intent Action
        const val TIMER_TICK = "TIMER_TICK"
        const val TIMER_STATUS = "TIMER_STATUS"
    }
}