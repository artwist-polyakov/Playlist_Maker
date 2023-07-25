package com.example.playlistmaker.presentation.player

import android.util.Log
import com.example.playlistmaker.domain.api.MediaPlayerInterface
import com.example.playlistmaker.data.repository.MediaPlayerImpl
import com.example.playlistmaker.domain.usecases.PlayButtonInteractUseCase
import com.example.playlistmaker.presentation.models.TrackInformation
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlayerPresenter(
    override var view: PlayerActivityInterface?,
    override var track: TrackInformation
) : PlayerPresenterInterface, MediaPlayerCallback {
    private var currentButtonState = READY_TO_PLAY_SHOW_PAUSE
    private lateinit var playButtonUseCase: PlayButtonInteractUseCase
    private var mediaPlayer: MediaPlayerInterface? = null

    companion object {
        private const val READY_TO_PLAY_SHOW_PAUSE = 0
        private const val READY_TO_PAUSE_SHOW_PLAY = 1
    }

    override fun changeTrack(track: TrackInformation) {
        Log.d(
            "currentButtonState",
            "changeTrack invokation currentButtonState = $currentButtonState"
        )
        view?.let {
            it.showPreparationState()
        }
        this.track = track
        mediaPlayer?.destroyPlayer()
        mediaPlayer = MediaPlayerImpl(this, track)
        currentButtonState = READY_TO_PLAY_SHOW_PAUSE
        Log.d(
            "currentButtonState",
            "after changeTrack invokation currentButtonState = $currentButtonState"
        )
    }

    override fun onPlayButtonClicked() {
        Log.d(
            "currentButtonState",
            "onPlayButtonClicked invokation currentButtonState = $currentButtonState"
        )
        if (currentButtonState == READY_TO_PLAY_SHOW_PAUSE) {
            currentButtonState = READY_TO_PAUSE_SHOW_PLAY
        } else {
            currentButtonState = READY_TO_PLAY_SHOW_PAUSE
        }
        showCurrentStage()
    }

    override fun setPlayPauseUseCase(fab: FloatingActionButton) {
        playButtonUseCase = PlayButtonInteractUseCase()
        fab.setOnClickListener {
            playButtonUseCase.execute(mediaPlayer)
        }
    }

    override fun showCurrentStage() {
        Log.d(
            "currentButtonState",
            "showCurrentStage invokation currentButtonState = $currentButtonState"
        )
        view?.let {
            if (currentButtonState == READY_TO_PLAY_SHOW_PAUSE) {
                it.showPauseState()
            } else {
                it.showPlayState()
            }
        }
    }

    override fun initPlayer() {
        Log.d(
            "currentButtonState",
            "initPlayer invokation currentButtonState = $currentButtonState"
        )
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayerImpl(this, track)
        } else {
            mediaPlayer?.let { mp ->
                if (mp.withTrack != track) {
                    changeTrack(track)
                }
                val currentPosition = TrackDurationTime(mp.getTrackPosition())
                view?.let {
                    it.setTime(currentPosition.toString())
                    showCurrentStage()
                }

            }
        }
    }

    override fun resetPlayer() {
        Log.d(
            "currentButtonState",
            "resetPlayer invokation currentButtonState = $currentButtonState"
        )

        mediaPlayer?.let {
            it.destroyPlayer()
        }
        currentButtonState = READY_TO_PLAY_SHOW_PAUSE
        view?.let {
            it.showPreparationState()
        }
        Log.d(
            "currentButtonState",
            "after resetPlayer invokation currentButtonState = $currentButtonState"
        )
    }

    override fun attachView(view: PlayerActivityInterface) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
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
