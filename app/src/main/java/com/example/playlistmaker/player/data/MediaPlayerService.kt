package com.example.playlistmaker.player.data

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.playlistmaker.root.ui.RootActivity


class MediaPlayerService : Service() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "Media player Service"
        const val INTENT_FILTER_PAUSE_ACTION = "INTENT_FILTER_PAUSE_ACTION"
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
            setSmallIcon(com.example.playlistmaker.R.mipmap.ic_launcher_round)
        }
        val notificationIntent = Intent().apply {
            setAction(INTENT_FILTER_PAUSE_ACTION)
        }
        val piPause = PendingIntent.getBroadcast(
            applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val pauseAction = NotificationCompat.Action.Builder(
            IconCompat.createWithResource(this, com.example.playlistmaker.R.drawable.pause_button),
            "Pause",
            piPause
        ).build()
        return notificationBuilder.addAction(pauseAction).build()
    }

    override fun onBind(intent: Intent): IBinder? = null


}


