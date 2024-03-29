package com.example.playlistmaker.player.presentation

import com.example.playlistmaker.common.presentation.models.TrackInformation

interface PlayerInterface {
    fun showTrackInfo(trackInfo: TrackInformation)
    fun showPlayState()
    fun showPauseState()
    fun showPreparationState()
    fun showReadyState()
    fun setTime(time: String)
}