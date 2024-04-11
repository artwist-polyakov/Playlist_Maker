package com.example.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.RingtoneManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R

internal class PlaylistMakerMusicService: Service() {
    private var songUrl = ""
    private val binder = MusicServiceBinder()
    private val handler = Handler(Looper.getMainLooper())
    private val soundRunnable = object : Runnable {
        override fun run() {
            playNotificationSound()
            // Планируем следующий запуск через 5 секунд
            handler.postDelayed(this, 5000)
        }
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): PlaylistMakerMusicService = this@PlaylistMakerMusicService
    }

    override fun onBind(intent: Intent?): IBinder? {
        songUrl = intent?.getStringExtra(EXTRA_URL_TAG) ?: ""
        Log.d(LOG_TAG, "onBind | url: $songUrl")
        handler.post(soundRunnable)
        createNotificationChannel()
        startForegroundWithServiceType()
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "onCreate")

    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy")
        handler.removeCallbacks(soundRunnable)
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(LOG_TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    private fun playNotificationSound() {
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(applicationContext, notificationSound)
        ringtone.play()
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