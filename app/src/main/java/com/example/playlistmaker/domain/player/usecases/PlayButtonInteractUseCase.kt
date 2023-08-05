package com.example.playlistmaker.domain.player.usecases

import android.util.Log
import com.example.playlistmaker.domain.player.api.MediaPlayerInterface
import com.example.playlistmaker.domain.common.usecases.UseCaseInterface

class PlayButtonInteractUseCase : UseCaseInterface<MediaPlayerInterface> {
    override fun execute(engine: MediaPlayerInterface?) {
        Log.d("currentButtonState", "PlayButtonInteractUseCase invokation")
        if (engine != null) engine.playPauseSwitcher()
    }
}