package com.example.playlistmaker.player.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.db.TracksDbInteractor
import com.example.playlistmaker.common.presentation.models.TrackDurationTime
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.common.presentation.models.TrackInformationToTrackMapper
import com.example.playlistmaker.common.presentation.models.TrackToTrackInformationMapper
import com.example.playlistmaker.player.domain.MediaPlayerCallbackInterface
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.TrackStorageInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class PlayerViewModel (
    private val trackStorageInteractor: TrackStorageInteractor,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val dbInteractor: TracksDbInteractor,
) : ViewModel(), MediaPlayerCallbackInterface {

    companion object {
        const val UPDATE_FREQUENCY = 250L
    }

    private var timerJob: Job? = null

    // LiveData для состояния проигрывателя
    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState
    private var lastState: PlayerState? = null

    private val _likeState = MutableLiveData<Boolean>()
    val likeState: LiveData<Boolean> get() = _likeState

    private val _timerState = MutableLiveData<TrackDurationTime>()
    val timerState: LiveData<TrackDurationTime> get() = _timerState
    private val lastTimerState: TrackDurationTime = TrackDurationTime(0)

    // MediaPlayer

    private var initializedTrack = trackStorageInteractor.giveMeLastTrack()

    // Функции для управления проигрыванием
    fun playPause() {
        mediaPlayerInteractor.playPauseSwitcher()
    }

    fun resetPlayer() {
        mediaPlayerInteractor.destroyPlayer()
    }

    override fun onMediaPlayerReady() {
        _playerState.value = PlayerState.Ready
        lastState = PlayerState.Ready
        _timerState.postValue(TrackDurationTime(0))
    }

    override fun onMediaPlayerPause() {
        _playerState.value = PlayerState.Pause
        lastState = PlayerState.Pause
        timerJob?.cancel()
    }

    override fun onMediaPlayerPlay() {
        _playerState.value = PlayerState.Play
        lastState = PlayerState.Play
        startTimer()
    }

    fun initializePlayer() {
        mediaPlayerInteractor.setCallback(this)
         giveCurrentTrack()?.let {
             mediaPlayerInteractor.initialize(it)
         }
    }

    fun giveCurrentTrack(): TrackInformation? {
        return initializedTrack
    }

    fun likeTrack() {
        viewModelScope.launch {
            initializedTrack?.let {
                val track = TrackInformationToTrackMapper().invoke(it)
                _likeState.postValue(dbInteractor.switchTrackLikeStatus(track))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.destroyPlayer()
    }

    fun restoreState(): Pair<PlayerState?, TrackDurationTime> {
        Log.d("restoreLikeState" ,"launched")
        restoreLikeState()
        return Pair(lastState, lastTimerState)
    }

    private fun restoreLikeState() {
        viewModelScope.launch {
            Log.d("restoreLikeState" ,"$_likeState")
            initializedTrack?.trackId?.let {
                val value  = dbInteractor.isTrackLiked(it)
                Log.d("restoreLikeState" ,"$value")
                _likeState.postValue(value)
            }
        }
    }

    fun makeItPause() {
        if(_playerState.value == PlayerState.Play) {
            mediaPlayerInteractor.playPauseSwitcher()
            timerJob?.cancel()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_playerState.value == PlayerState.Play) {
                delay(UPDATE_FREQUENCY)
                _timerState.postValue(TrackDurationTime(mediaPlayerInteractor.getCurrentPosition()))
            }
        }
    }
}