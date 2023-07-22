package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.domain.models.Track

interface PlayerPresenterInterface {
    var view: PlayerActivityInterface?
    var track: Track

    fun bindScreen()


    fun changePlayButton()

    fun resetPlayer()

    fun pausePresenter()

    fun changeView(newView: PlayerActivityInterface)

    fun changeTrack(track: Track)

}