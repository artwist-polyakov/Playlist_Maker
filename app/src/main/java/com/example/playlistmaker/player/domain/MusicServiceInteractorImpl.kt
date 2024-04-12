package com.example.playlistmaker.player.domain

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.playlistmaker.player.service.PlayerServiceState
import com.example.playlistmaker.player.service.PlaylistMakerMusicService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MusicServiceInteractorImpl(
    val storage: TrackStorageInteractor,
    val context: Context
) : MusicServiceInteractor {
    private val _musicServiceFlow = MutableSharedFlow<PlayerServiceState>(replay = 1)
    val musicServiceFlow: SharedFlow<PlayerServiceState> =
        _musicServiceFlow.asSharedFlow()

    private var musicService: PlaylistMakerMusicService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlaylistMakerMusicService.MusicServiceBinder
            musicService = binder.getService().also {
                it.playerState.onEach { state ->
                    _musicServiceFlow.emit(state)
                }.launchIn(CoroutineScope(Dispatchers.IO))
            }
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

    override fun configureAndLaunchService(): SharedFlow<PlayerServiceState> {
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
        return musicServiceFlow
    }

    override fun unBindService() {
        context.unbindService(serviceConnection)
    }
}