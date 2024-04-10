package com.example.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R

internal class PlaylistMakerMusicService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "onCreate")
        createNotificationChannel()
        startForegroundWithServiceType()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "onStartCommand | flags: $flags, startId: $startId")
        val url = intent?.getStringExtra(EXTRA_URL_TAG)
        Log.d(LOG_TAG, "onStartCommand | url: $url")
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        // Создание каналов доступно только с Android 8.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            /* id= */ NOTIFICATION_CHANNEL_ID,
            /* name= */ "Music service",
            /* importance= */ NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"

        // Регистрируем канал уведомлений
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Music foreground service")
            .setContentText("Our service is working right now!")
            .setSmallIcon(R.drawable.ic_media)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun startForegroundWithServiceType() {
        val notification = createServiceNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                SERVICE_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(
                SERVICE_NOTIFICATION_ID,
                notification
            )
        }
    }

    companion object   {
        const val EXTRA_URL_TAG = "trackUrl"
        const val LOG_TAG = "PlaylistMakerMusicService"
        const val NOTIFICATION_CHANNEL_ID = "PlaylistMakerMusicServiceChannel"
        const val SERVICE_NOTIFICATION_ID = 100
    }

}