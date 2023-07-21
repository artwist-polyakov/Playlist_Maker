package com.example.playlistmaker.presentation.player

interface PlayerPresenterInterface {
    var view: PlayerActivityInterface?


    fun bindScreen()

    fun setTime()

    fun changePlayButton()

    fun resetPlayer()
}