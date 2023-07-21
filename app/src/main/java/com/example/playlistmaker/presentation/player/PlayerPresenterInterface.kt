package com.example.playlistmaker.presentation.player

interface PlayerPresenterInterface {
    var view: PlayerActivityInterface?

    fun bindScreen()


    fun changePlayButton()

    fun resetPlayer()

    fun pausePresenter()
}