package com.example.playlistmaker.player.domain

import com.example.playlistmaker.common.presentation.models.TrackInformation

interface MediaPlayerInteractor {
    fun playPauseSwitcher()
    fun destroyPlayer()
    fun setCallback(callback: MediaPlayerCallbackInterface)
    fun initialize(track: TrackInformation)

}