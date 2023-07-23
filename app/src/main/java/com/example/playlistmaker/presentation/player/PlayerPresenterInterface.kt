package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.models.TrackInformation

interface PlayerPresenterInterface {
    var view: PlayerActivityInterface?
    var track: TrackInformation

    fun bindScreen()


    fun changePlayButton()

    fun resetPlayer()

    fun pausePresenter()

    fun changeView(newView: PlayerActivityInterface)

    fun changeTrack(track: TrackInformation)

}