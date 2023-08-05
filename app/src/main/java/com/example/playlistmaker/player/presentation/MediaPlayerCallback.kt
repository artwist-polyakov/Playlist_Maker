package com.example.playlistmaker.player.presentation

import com.example.playlistmaker.common.presentation.models.TrackDurationTime

interface MediaPlayerCallback {
    fun onMediaPlayerReady()
    fun onMediaPlayerTimeUpdate(time: TrackDurationTime)
    fun resetPlayer()
    fun onPlayButtonClicked()

}