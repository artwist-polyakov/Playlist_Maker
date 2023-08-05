package com.example.playlistmaker.domain.player.api

import com.example.playlistmaker.presentation.common.models.TrackInformation
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