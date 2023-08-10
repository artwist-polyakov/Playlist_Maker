package com.example.playlistmaker.player.domain

import com.example.playlistmaker.common.presentation.models.TrackDurationTime

interface MediaPlayerCallbackInterface {
    fun onMediaPlayerReady()
    fun onMediaPlayerTimeUpdate(time: TrackDurationTime)
    fun onMediaPlayerPause()
    fun onMediaPlayerPlay()
}