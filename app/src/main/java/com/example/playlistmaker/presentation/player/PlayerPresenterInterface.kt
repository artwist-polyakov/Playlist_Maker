package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.presentation.common.models.TrackInformation
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