package com.example.playlistmaker.player.domain

interface MusicServiceInteractor {

    fun play()
    fun pause()
    fun stop()
    fun configureAndLaunchService()

}