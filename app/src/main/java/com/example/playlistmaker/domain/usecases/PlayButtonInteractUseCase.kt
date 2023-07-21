package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.api.MediaPlayerInterface

class PlayButtonInteractUseCase (val player: MediaPlayerInterface?) {
    fun execute() {
        player?.playPauseSwitcher()
    }
}