package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.api.MediaPlayerInterface
import com.example.playlistmaker.presentation.player.MediaPlayerCallback
import com.example.playlistmaker.ui.player.PlayerActivity
import javax.security.auth.callback.Callback

class NextMediaPlayer(override var callback: MediaPlayerCallback? = null,
                      override var withTrack: TrackDto?) : MediaPlayer(), MediaPlayerInterface {
    private var state = STATE_DEFAULT
    private var handler = Handler(Looper.getMainLooper())
    private var updateProgressRunnable: Runnable = Runnable { }
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
                    state = STATE_PREPARED
                    callback?.onMediaPlayerReady()
                }
                setOnCompletionListener {
                    callback?.resetPlayer()
                    state = STATE_PREPARED
                    handler.removeCallbacks(updateProgressRunnable)
                }
            }
        }
    }

    override fun playPauseSwitcher() {
        playbackControl()
    }

    override fun destroyPlayer() {
        if (state != 0) {
            this.release()
        }
        state = STATE_DEFAULT
    }

    override fun updateProgress(callback: Callback) {
        val time = this.currentPosition
        updateProgressRunnable = Runnable { updateProgress(callback) }
        handler.postDelayed(updateProgressRunnable, 400)

    }


    private fun playbackControl() {
        when (state) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun startPlayer() {
        this.start()
        callback?.changePlayButton()
        state = STATE_PLAYING
        updateProgressRunnable = Runnable { callback?.onMediaPlayerTimeUpdate(this.currentPosition) }
        handler.postDelayed(updateProgressRunnable, 400)
    }

    override fun pausePlayer() {
        this.pause()
        callback?.changePlayButton()
        state = STATE_PAUSED
        handler.removeCallbacks(updateProgressRunnable)
    }


}