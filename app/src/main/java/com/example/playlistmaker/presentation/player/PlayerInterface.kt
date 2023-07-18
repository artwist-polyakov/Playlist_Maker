package com.example.playlistmaker.presentation.player

import androidx.constraintlayout.widget.Group
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track

interface PlayerInterface {
    var trackCountryInfoGroup: Group?
    var playerPresenter: PlayerPresenterInterface?
    var currentTrack: TrackDto?
    fun bindScreen()

    fun play()

    fun pause()
}