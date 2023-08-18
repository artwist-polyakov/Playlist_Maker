package com.example.playlistmaker.player.domain

import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.player.domain.MediaPlayerCallbackInterface
import com.example.playlistmaker.player.domain.TrackStorageInteractor
import com.example.playlistmaker.search.data.storage.TracksStorage

interface MediaPlayerInterface {

    fun playPauseSwitcher()

    fun destroyPlayer()

    fun updateProgress(callback: MediaPlayerCallbackInterface)

    fun startPlayer()

    fun pausePlayer()

    fun getTrackPosition(): Int

    fun setTrackPosition(position: Int)

    fun forceInit(track: TrackInformation)

    fun setCallback(callback: MediaPlayerCallbackInterface)


}