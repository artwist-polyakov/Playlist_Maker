package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.service.PlayerServiceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface MusicServiceInteractor {

    fun play()
    fun pause()
    fun stop()
    fun configureAndLaunchService(): SharedFlow<PlayerServiceState>
    fun unBindService()

}