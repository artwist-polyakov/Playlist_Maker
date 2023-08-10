package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.player.domain.MediaPlayerCallbackInterface
import com.example.playlistmaker.player.presentation.MediaPlayerCallback
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel

interface MediaPlayerInterface {
    var withTrack: TrackInformation?
    var callback: MediaPlayerCallbackInterface?

    fun playPauseSwitcher()

    fun destroyPlayer()

    fun updateProgress(callback: MediaPlayerCallbackInterface)

    fun startPlayer()

    fun pausePlayer()

    fun getTrackPosition(): Int

    fun setTrackPosition(position: Int)


}