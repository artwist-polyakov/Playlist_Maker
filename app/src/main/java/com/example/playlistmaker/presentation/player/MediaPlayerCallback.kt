package com.example.playlistmaker.presentation.player

interface MediaPlayerCallback {
    fun onMediaPlayerReady()
    fun onMediaPlayerTimeUpdate(time: TrackDurationTime)

    fun resetPlayer()

    fun changePlayButton()
}