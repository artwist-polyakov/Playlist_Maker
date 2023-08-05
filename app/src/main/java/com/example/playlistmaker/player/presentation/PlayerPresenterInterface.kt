package com.example.playlistmaker.player.presentation

import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.google.android.material.floatingactionbutton.FloatingActionButton

interface PlayerPresenterInterface {
    var view: PlayerActivityInterface?
    var track: TrackInformation

    fun initPlayer()
    fun resetPlayer()
    fun changeTrack(track: TrackInformation)
    fun onPlayButtonClicked()
    fun setPlayPauseUseCase(button: FloatingActionButton)
    fun showCurrentStage()

    fun attachView(view: PlayerActivityInterface)

    fun detachView()


}