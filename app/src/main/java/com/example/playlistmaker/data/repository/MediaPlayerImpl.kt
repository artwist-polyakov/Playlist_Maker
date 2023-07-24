package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.api.MediaPlayerInterface
import com.example.playlistmaker.presentation.models.TrackInformation
import com.example.playlistmaker.presentation.player.TrackDurationTime
import com.example.playlistmaker.presentation.player.MediaPlayerCallback


class MediaPlayerImpl(override var callback: MediaPlayerCallback? = null,
                      override var withTrack: TrackInformation?) : MediaPlayer(), MediaPlayerInterface {

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



    init {
        withTrack.let {
            this.apply {
                setDataSource(it?.previewUrl)
                prepareAsync()
                setOnPreparedListener{
                    duration = it.duration
                    state = STATE_PREPARED
                    callback?.onMediaPlayerReady()
                }
                // TODO сделать через родную реализацию остановку проигрывания
//                setOnCompletionListener {
//                    if (state == STATE_PLAYING) {
//                        finishPlay()
//                    }
//                }

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
        if (state != 0) {
            this.release()
        }
        state = STATE_DEFAULT
    }

    override fun updateProgress(callback: MediaPlayerCallback) {
        handler.postDelayed(updateProgressRunnable, UPDATE_STEP_400MS_LONG)
    }


    override fun startPlayer() {
        this.start()
        callback?.onPlayButtonClicked()
        state = STATE_PLAYING
        callback?.let {
            it.onMediaPlayerTimeUpdate(TrackDurationTime(customCurrentPosition))
            updateProgress(it)}
    }


    override fun pausePlayer() {
        this.pause()
        callback?.onPlayButtonClicked()
        state = STATE_PAUSED
        handler.removeCallbacks(updateProgressRunnable)
    }

    private fun finishPlay() {
        this.pausePlayer()
        customCurrentPosition = 0
        this.seekTo(customCurrentPosition)
        state = STATE_PREPARED
        callback?.onMediaPlayerTimeUpdate(TrackDurationTime(customCurrentPosition))
        handler.removeCallbacks(updateProgressRunnable)
    }

    override fun getTrackPosition(): Int {
        return customCurrentPosition
    }

    override fun setTrackPosition(position: Int) {
        customCurrentPosition = position
        this.seekTo(customCurrentPosition)
    }

    override fun changeTrack(track: TrackInformation) {
        this.apply {
            this.reset()
            finishPlay()
            setDataSource(track.previewUrl)
            prepareAsync()
            setOnPreparedListener{
                duration = it.duration
                state = STATE_PREPARED
                callback?.onMediaPlayerReady()
            }
        }
    }


}