package com.example.playlistmaker.domain.usecases

import android.util.Log
import com.example.playlistmaker.domain.api.MediaPlayerInterface

class PlayButtonInteractUseCase : UseCaseInterface<MediaPlayerInterface> {
    override fun execute(engine: MediaPlayerInterface?) {
        Log.d("currentButtonState", "PlayButtonInteractUseCase invokation")
        if (engine != null) engine.playPauseSwitcher()
    }
}