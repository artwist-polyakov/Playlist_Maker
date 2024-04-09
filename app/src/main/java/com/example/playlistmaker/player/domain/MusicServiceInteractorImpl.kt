package com.example.playlistmaker.player.domain

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.playlistmaker.player.service.PlaylistMakerMusicService

class MusicServiceInteractorImpl (
    val storage: TrackStorageInteractor,
    val context: Context
    ) : MusicServiceInteractor {
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
                PlaylistMakerMusicService.EXTRA_URL_TAG,
                storage.giveMeLastTrack().previewUrl
            )
        }
        Log.d(
            PlaylistMakerMusicService.LOG_TAG,
            "Interactor launches service"
        )
        ContextCompat.startForegroundService(context, intent)
    }
}