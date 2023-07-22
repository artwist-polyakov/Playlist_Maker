package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.player.MediaPlayerCallback

interface MediaPlayerInterface {
    var withTrack: Track?
    var callback: MediaPlayerCallback?

    fun playPauseSwitcher()

    fun destroyPlayer()

    fun updateProgress(callback: MediaPlayerCallback)

    fun startPlayer()

    fun pausePlayer()

    fun getTrackPosition(): Int

    fun setTrackPosition(position: Int)

    fun changeTrack(track: Track)

}