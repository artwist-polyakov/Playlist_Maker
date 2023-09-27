package com.example.playlistmaker.player.domain

import com.example.playlistmaker.common.presentation.models.TrackInformation

interface MediaPlayerInterface {
    fun playPauseSwitcher()
    fun destroyPlayer()
    fun startPlayer()
    fun pausePlayer()
    fun getTrackPosition(): Int
    fun forceInit(track: TrackInformation)
    fun setCallback(callback: MediaPlayerCallbackInterface)
}