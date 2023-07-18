package com.example.playlistmaker.data.dto

import android.media.MediaPlayer
import com.example.playlistmaker.domain.impl.MediaPlayerInterface

class NextMediaPlayer: MediaPlayer(), MediaPlayerInterface {
    private var state = STATE_DEFAULT
    override fun prepare() {
        super.prepare()
        state = STATE_PREPARED
    }

    override fun start() {
        super.start()
        state = STATE_PLAYING
    }

    override fun pause() {
        super.pause()
        state = STATE_PAUSED
    }

    override fun reset() {
        super.reset()
        state = STATE_DEFAULT
    }

    override fun stop() {
        super.stop()
        state = STATE_DEFAULT
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}