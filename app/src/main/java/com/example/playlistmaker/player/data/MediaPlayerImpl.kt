package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.MediaPlayerInterface
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.player.domain.MediaPlayerCallbackInterface


class MediaPlayerImpl : MediaPlayer(), MediaPlayerInterface {
    private var callback: MediaPlayerCallbackInterface? = null
    private var track: TrackInformation? = null
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val UPDATE_STEP_250MS_LONG = 250L
    }

    private var state = STATE_DEFAULT

    override fun playPauseSwitcher() {
        when (state) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun destroyPlayer() {
        if (state != STATE_DEFAULT) {
            this.release()
        }
        state = STATE_DEFAULT
    }

    override fun startPlayer() {
        this.start()
        state = STATE_PLAYING
        callback?.onMediaPlayerPlay()
    }

    override fun pausePlayer() {
        if (state == STATE_PLAYING) {
            this.pause()
            state = STATE_PAUSED
            callback?.onMediaPlayerPause()
        }
    }

    private fun finishPlay() {
        this.pausePlayer()
        state = STATE_PREPARED
        callback?.onMediaPlayerReady()
    }

    override fun getTrackPosition(): Int {
        return currentPosition
    }

    override fun forceInit(track: TrackInformation) {
        setTrack(track)
        this.track?.let { tr ->
            let { mp ->
                mp.setDataSource(tr.previewUrl)
                setupCompletionListener()
                mp.prepareAsync()
                mp.setOnPreparedListener {
                    state = STATE_PREPARED
                    callback?.onMediaPlayerReady()
                }
            }
        }
    }

    override fun setCallback(callback: MediaPlayerCallbackInterface) {
        this.callback = callback
    }

    private fun setTrack(track: TrackInformation) {
        this.track = track
    }

    private fun setupCompletionListener() {
        this.setOnCompletionListener {
            finishPlay()
        }
    }
}