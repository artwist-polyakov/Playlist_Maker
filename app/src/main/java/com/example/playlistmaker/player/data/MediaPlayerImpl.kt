package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.playlistmaker.player.domain.MediaPlayerInterface
import com.example.playlistmaker.common.presentation.models.TrackDurationTime
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
        private const val UPDATE_STEP_400MS_LONG = 400L
    }

    private var state = STATE_DEFAULT
    private var handler = Handler(Looper.getMainLooper())
    private var customCurrentPosition = 0
    private var duration = 0

    private val updateProgressRunnable: Runnable = object : Runnable {
        override fun run() {
            if (state == STATE_PLAYING) {
                customCurrentPosition += UPDATE_STEP_400MS_LONG.toInt()
                val time = TrackDurationTime(customCurrentPosition)
                callback?.onMediaPlayerTimeUpdate(time)
                if (customCurrentPosition >= duration) {
                    finishPlay()
                }
                handler.postDelayed(this, UPDATE_STEP_400MS_LONG)
            }
        }
    }

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

    override fun updateProgress(callback: MediaPlayerCallbackInterface) {
        callback.onMediaPlayerTimeUpdate(TrackDurationTime(customCurrentPosition))
        handler.postDelayed(updateProgressRunnable, UPDATE_STEP_400MS_LONG)
    }

    override fun startPlayer() {
        this.start()
        state = STATE_PLAYING
        callback?.onMediaPlayerPlay()
        handler.postDelayed(updateProgressRunnable, UPDATE_STEP_400MS_LONG)
    }

    override fun pausePlayer() {
        if (state == STATE_PLAYING) {
            this.pause()
            state = STATE_PAUSED
            handler.removeCallbacks(updateProgressRunnable)
            callback?.onMediaPlayerPause()
        }
    }

    private fun finishPlay() {
        this.pausePlayer()
        customCurrentPosition = 0
        state = STATE_PREPARED
        callback?.onMediaPlayerTimeUpdate(TrackDurationTime(customCurrentPosition))

    }

    override fun getTrackPosition(): Int {
        return customCurrentPosition
    }

    override fun setTrackPosition(position: Int) {
        customCurrentPosition = position
        this.seekTo(customCurrentPosition)
    }

    override fun forceInit(track: TrackInformation) {
        setTrack(track)
        this.track?.let { tr ->
            let { mp ->
                mp.setDataSource(tr.previewUrl)
                mp.prepareAsync()
                mp.setOnPreparedListener {
                    this.duration = mp.getDuration()
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

}