package com.example.playlistmaker.player.ui.view_model

import com.example.playlistmaker.common.presentation.models.TrackDurationTime

interface MediaPlayerViewModelCallback {
    fun onMediaPlayerReady()
    fun onMediaPlayerTimeUpdate(time: TrackDurationTime)
}