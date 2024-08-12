package com.example.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.StringBuilder


internal class PlaylistMakerMusicService : Service() {

    private var playlistPlayer: MediaPlayer? = null

    private var track: TrackInformation? = null
    private val binder = MusicServiceBinder()
    private val _playerState = MutableStateFlow<PlayerServiceState>(PlayerServiceState.Default())
    val playerState = _playerState.asStateFlow()
    private var timerJob: Job? = null

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (playlistPlayer?.isPlaying == true) {
                delay(300L)
                _playerState.value = PlayerServiceState.Playing(getCurrentPlayerPosition())
            }
        }
    }

    private fun getCurrentPlayerPosition(): Int {
        return playlistPlayer?.currentPosition ?: 0
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): PlaylistMakerMusicService = this@PlaylistMakerMusicService
    }

    override fun onBind(intent: Intent?): IBinder? {

        track = try {
            Gson().fromJson(
                intent?.getStringExtra(EXTRA_TRACK_TAG),
                TrackInformation::class.java
            )
        } catch (e: Exception) {
            null
        }
        createNotificationChannel()
//        startForegroundWithServiceType()
        initMediaPlayer()
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        playlistPlayer = MediaPlayer()

    }

    private fun initMediaPlayer() {
        if (track == null) {
            return
        }
        playlistPlayer?.apply {
            setDataSource(track?.previewUrl)
            prepareAsync()
            setOnPreparedListener {
                _playerState.value = PlayerServiceState.Prepared()
            }
            setOnCompletionListener {
                pausePlayer()
                _playerState.value = PlayerServiceState.Prepared()
                stopNotification()
            }
        }
    }

    fun stopNotification() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun startPlayer() {
        playlistPlayer?.start()
        _playerState.value = PlayerServiceState.Playing(getCurrentPlayerPosition())
        startTimer()
    }

    fun pausePlayer() {
        playlistPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerServiceState.Paused(getCurrentPlayerPosition())
    }


    private fun releasePlayer() {
        playlistPlayer?.stop()
        timerJob?.cancel()
        _playerState.value = PlayerServiceState.Default()
        playlistPlayer?.setOnPreparedListener(null)
        playlistPlayer?.setOnCompletionListener(null)
        playlistPlayer?.release()
        playlistPlayer = null
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {

        releasePlayer()
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        val trackName = StringBuilder()
            .append(track?.artistName ?: "Unknown artist")
            .append(" - ")
            .append(track?.trackName ?: "Unknown track")
            .toString()
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText(trackName)
            .setSmallIcon(R.drawable.ic_media)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    fun startForegroundWithServiceType() {
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

    companion object {
        const val EXTRA_TRACK_TAG = "track"
        const val LOG_TAG = "PlaylistMakerMusicService"
        const val NOTIFICATION_CHANNEL_ID = "PlaylistMakerMusicServiceChannel"
        const val SERVICE_NOTIFICATION_ID = 100
    }

}