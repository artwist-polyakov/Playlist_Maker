package com.example.playlistmaker.player.data

import com.example.playlistmaker.common.presentation.models.TrackDurationTime
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.player.domain.MediaPlayerCallbackInterface
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.MediaPlayerInterface

class MediaPlayerInteractorImpl(private val mediaPlayer: MediaPlayerInterface): MediaPlayerInteractor {
    override fun playPauseSwitcher() {
        mediaPlayer.playPauseSwitcher()
    }
    override fun destroyPlayer() {
        mediaPlayer.destroyPlayer()
    }

    override fun setCallback(callback: MediaPlayerCallbackInterface) {
        mediaPlayer.setCallback(callback)
    }

    override fun initialize(track: TrackInformation) {
        mediaPlayer.forceInit(track)
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.getTrackPosition()
    }
}