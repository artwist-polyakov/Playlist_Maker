package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.data.dto.TrackDto

interface PlayerPresenterInterface {
    var view: PlayerActivityInterface?
    var track: TrackDto

    fun bindScreen()


    fun changePlayButton()

    fun resetPlayer()

    fun pausePresenter()
}