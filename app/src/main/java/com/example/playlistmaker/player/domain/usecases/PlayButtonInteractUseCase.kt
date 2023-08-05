package com.example.playlistmaker.player.domain.usecases

import android.util.Log
import com.example.playlistmaker.player.domain.api.MediaPlayerInterface
import com.example.playlistmaker.common.domain.usecases.UseCaseInterface

class PlayButtonInteractUseCase : UseCaseInterface<MediaPlayerInterface> {
    override fun execute(engine: MediaPlayerInterface?) {
        Log.d("currentButtonState", "PlayButtonInteractUseCase invokation")
        if (engine != null) engine.playPauseSwitcher()
    }
}