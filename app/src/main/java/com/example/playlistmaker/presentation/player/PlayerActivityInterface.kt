package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.presentation.models.TrackInformation

interface PlayerActivityInterface {
    fun showTrackInfo(trackInfo: TrackInformation)
    fun showPlayState()
    fun showPauseState()

    fun showPreparationState()

    fun showReadyState()

    fun setTime(time: String)

}