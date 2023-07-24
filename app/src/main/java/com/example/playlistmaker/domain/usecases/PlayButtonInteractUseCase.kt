package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.api.MediaPlayerInterface

class PlayButtonInteractUseCase: UseCaseInterface<MediaPlayerInterface> {
    override fun execute(engine: MediaPlayerInterface?) {
        if (engine != null) engine.playPauseSwitcher()
    }
}