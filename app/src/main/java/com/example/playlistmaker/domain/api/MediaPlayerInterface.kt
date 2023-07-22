package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.presentation.player.MediaPlayerCallback
import javax.security.auth.callback.Callback

interface MediaPlayerInterface {
    var withTrack: TrackDto?
    var callback: MediaPlayerCallback?

    fun playPauseSwitcher()

    fun destroyPlayer()

    fun updateProgress(callback: MediaPlayerCallback)

    fun startPlayer()

    fun pausePlayer()

    fun getTrackPosition(): Int

    fun setTrackPosition(position: Int)

    fun changeTrack(track: TrackDto)

}