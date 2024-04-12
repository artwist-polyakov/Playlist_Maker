package com.example.playlistmaker.player.domain

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.playlistmaker.player.service.PlaylistMakerMusicService
import com.google.gson.Gson

class MusicServiceInteractorImpl(
    val storage: TrackStorageInteractor,
    val context: Context
) : MusicServiceInteractor {

    private var musicService: PlaylistMakerMusicService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlaylistMakerMusicService.MusicServiceBinder
            musicService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun configureAndLaunchService() {
        val intent = Intent(
            context,
            PlaylistMakerMusicService::class.java
        ).apply {

            putExtra(
                PlaylistMakerMusicService.EXTRA_TRACK_TAG,
                Gson().toJson(storage.giveMeLastTrack())
            )
        }
        Log.d(
            PlaylistMakerMusicService.LOG_TAG,
            "Interactor launches service"
        )
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun unBindService() {
        context.unbindService(serviceConnection)
    }
}