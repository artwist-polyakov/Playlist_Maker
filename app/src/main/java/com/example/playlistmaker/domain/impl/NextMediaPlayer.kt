package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.MediaPlayerInterface
import javax.security.auth.callback.Callback

class NextMediaPlayer: MediaPlayer(), MediaPlayerInterface {
    private var state = STATE_DEFAULT

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    override fun playPauseSwitcher() {
        TODO("Not yet implemented")
    }

    override fun destroyPlayer() {
        TODO("Not yet implemented")
    }

    override fun updateProgress(callback: Callback) {
        TODO("Not yet implemented")
    }
}