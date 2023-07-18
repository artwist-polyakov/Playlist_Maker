package com.example.playlistmaker.presentation.player

interface PlayerPresenterInterface {
    var view: PlayerInterface?

    fun play()
    fun pause()

    fun bindScreen()

    fun setTime()

    fun changePlayButton()

    fun resetPlayer()
}