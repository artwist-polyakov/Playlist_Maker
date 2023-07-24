package com.example.playlistmaker.presentation.player

import android.util.Log
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.api.MediaPlayerInterface
import com.example.playlistmaker.data.repository.MediaPlayerImpl
import com.example.playlistmaker.domain.usecases.PlayButtonInteractUseCase
import com.example.playlistmaker.domain.usecases.UseCaseInterface
import com.example.playlistmaker.presentation.models.TrackInformation
import com.example.playlistmaker.ui.player.PlayerActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class PlayerPresenter(
    override var view: PlayerActivityInterface?,
    override var track: TrackInformation
) : PlayerPresenterInterface, MediaPlayerCallback {
    private var currentButtonState = READY_TO_PLAY
    private lateinit var playButtonUseCase: PlayButtonInteractUseCase
    private var mediaPlayer: MediaPlayerInterface? = null

    companion object {
        private const val READY_TO_PLAY = 0
        private const val READY_TO_PAUSE = 1
    }

    override fun pausePresenter() {
        if (currentButtonState == READY_TO_PAUSE) {
            mediaPlayer?.pausePlayer()
        }
    }

    override fun changeView(newView: PlayerActivityInterface) {
        this.view = newView
    }

    private fun setPlayIcon() {
        view?.let {
            it.showPauseState()
        }
    }

    override fun changeTrack(track: TrackInformation) {
        view?.let {
            it.showPreparationState()
        }
        this.track = track
        mediaPlayer?.changeTrack(track)
        currentButtonState = READY_TO_PLAY
        setPlayIcon()
    }

    override fun onPlayButtonClicked() {
        Log.d("PLAYPAUSE", "currentButtonState = $currentButtonState")
        showCurrentStage()
    }

    override fun setPlayPauseUseCase(fab: FloatingActionButton) {
        playButtonUseCase = PlayButtonInteractUseCase()
        fab.setOnClickListener {
            playButtonUseCase.execute(mediaPlayer)
        }
    }

    override fun showCurrentStage() {
        view?.let{
            if (currentButtonState == READY_TO_PLAY) {
                it.showPauseState()
                currentButtonState = READY_TO_PAUSE
            } else {
                it.showPlayState()
                currentButtonState = READY_TO_PLAY
            }
        }
    }

    override fun initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayerImpl(this, track)
        } else {
            mediaPlayer?.let{ mp ->
                mp.changeTrack(track)
                val currentPosition = TrackDurationTime(mp.getTrackPosition())
                view?.let {
                    it.setTime(currentPosition.toString())
                    showCurrentStage()
                }

            }
        }
    }

    override fun resetPlayer() {
        if (currentButtonState == READY_TO_PAUSE) {
            mediaPlayer?.destroyPlayer()
            mediaPlayer = null
        }
        view?.let {
            it.setTime("10:00")
        }
    }

    override fun onMediaPlayerReady() {
        view?.let {
            it.showReadyState()
        }
    }

    override fun onMediaPlayerTimeUpdate(time: TrackDurationTime) {
        view?.let {
            it.setTime(time.toString())
        }
    }
}
