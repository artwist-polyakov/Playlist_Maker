package com.example.playlistmaker.player.data

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R

class MediaPlayerService : Service() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "Media player Service"
        const val ACTION_START_FOREGROUND = "ACTION_START_FOREGROUND"
        const val ACTION_STOP_FOREGROUND = "ACTION_STOP_FOREGROUND"
        const val EXTRA_ARTIST = "EXTRA_ARTIST"
        const val EXTRA_TITLE = "EXTRA_TITLE"
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_FOREGROUND -> {
                val artist = intent.getStringExtra(EXTRA_ARTIST).orEmpty()
                val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
                startForeground(1, createNotification(artist, title))
            }

            ACTION_STOP_FOREGROUND -> {
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun createNotification(artist: String, title: String): Notification {
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setContentTitle(title)
            setContentText(artist)
            setSmallIcon(R.mipmap.ic_launcher_round)
        }
        return notificationBuilder.build()
    }

    override fun onBind(intent: Intent): IBinder? = null


}


