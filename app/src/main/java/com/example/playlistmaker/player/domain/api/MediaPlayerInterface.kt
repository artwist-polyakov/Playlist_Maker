package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.player.presentation.MediaPlayerCallback

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