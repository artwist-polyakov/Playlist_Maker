package com.example.playlistmaker.presentation.player

interface PlayerPresenterInterface {
    var view: PlayerInterface?

    fun play()
    fun pause()

}