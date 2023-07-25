package com.example.playlistmaker.domain.api

import com.example.playlistmaker.presentation.models.TrackInformation
import com.example.playlistmaker.presentation.player.MediaPlayerCallback

interface MediaPlayerInterface {
    var withTrack: TrackInformation?
    var callback: MediaPlayerCallback?

    fun playPauseSwitcher()

    fun destroyPlayer()

    fun updateProgress(callback: MediaPlayerCallback)

    fun startPlayer()

    fun pausePlayer()

    fun getTrackPosition(): Int

    fun setTrackPosition(position: Int)


}