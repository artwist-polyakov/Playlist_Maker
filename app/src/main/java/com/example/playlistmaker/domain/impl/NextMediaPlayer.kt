package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.api.MediaPlayerInterface
import com.example.playlistmaker.domain.models.TrackDurationTime
import com.example.playlistmaker.presentation.player.MediaPlayerCallback


class NextMediaPlayer(override var callback: MediaPlayerCallback? = null,
                      override var withTrack: TrackDto?) : MediaPlayer(), MediaPlayerInterface {
    private var state = STATE_DEFAULT
    private var handler = Handler(Looper.getMainLooper())
    private var customCurrentPosition = 0
    private var duration = 0

    private val updateProgressRunnable: Runnable = object : Runnable {
        override fun run() {
            if (state == STATE_PLAYING) {
                customCurrentPosition += 400
                val time = TrackDurationTime(customCurrentPosition)
//                Log.d("UPDATE PROGRESS", "updateProgress: ${time.toString()}")
                callback?.onMediaPlayerTimeUpdate(time)
                if (customCurrentPosition >= duration) {
                    finishPlay()
                }
                handler.postDelayed(this, 400)
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
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
        handler.postDelayed(updateProgressRunnable, 400)
    }


    override fun startPlayer() {
        this.start()
        callback?.changePlayButton()
        state = STATE_PLAYING
        callback?.let { updateProgress(it) }
    }


    override fun pausePlayer() {
        this.pause()
        callback?.changePlayButton()
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


}