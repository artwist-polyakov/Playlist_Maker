package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.presentation.models.TrackInformation
import com.google.android.material.floatingactionbutton.FloatingActionButton

interface PlayerPresenterInterface {
    var view: PlayerActivityInterface?
    var track: TrackInformation

    fun initPlayer()

    fun resetPlayer()

    fun changeView(view: PlayerActivityInterface)

    fun changeTrack(track: TrackInformation)

    fun onPlayButtonClicked()

    fun setPlayPauseUseCase(button: FloatingActionButton)

    fun showCurrentStage()

}