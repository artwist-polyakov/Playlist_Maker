package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.service.PlayerServiceState
import kotlinx.coroutines.flow.SharedFlow

interface MusicServiceInteractor {

    fun play()
    fun pause()

    fun showNotification()

    fun hideNotification()

    fun configureAndLaunchService(): SharedFlow<PlayerServiceState>
    fun unBindService()

}