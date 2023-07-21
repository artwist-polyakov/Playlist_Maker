package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.domain.models.TrackDurationTime

interface MediaPlayerCallback {
    fun onMediaPlayerReady()
    fun onMediaPlayerTimeUpdate(time: Int)

    fun resetPlayer()

    fun changePlayButton()
}