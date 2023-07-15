package com.example.playlistmaker.presentation.player

import androidx.constraintlayout.widget.Group
import com.example.playlistmaker.data.dto.TrackDto

interface PlayerInterface {
    var trackCountryInfoGroup: Group?
    var playerPresenter: PlayerPresenterInterface
    fun bindScreen(from: TrackDto)
}