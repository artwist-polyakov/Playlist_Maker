package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.presentation.common.models.TrackDurationTime

interface MediaPlayerCallback {
    fun onMediaPlayerReady()
    fun onMediaPlayerTimeUpdate(time: TrackDurationTime)
    fun resetPlayer()
    fun onPlayButtonClicked()

}