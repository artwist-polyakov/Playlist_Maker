package com.example.playlistmaker.player.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

internal class PlaylistMakerMusicService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "onStartCommand | flags: $flags, startId: $startId")
        val url = intent?.getStringExtra(EXTRA_URL_TAG)
        Log.d(LOG_TAG, "onStartCommand | url: $url")
        return Service.START_NOT_STICKY
    }

    companion object   {
        const val EXTRA_URL_TAG = "trackUrl"
        const val LOG_TAG = "PlaylistMakerMusicService"
    }

}