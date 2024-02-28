@file:Suppress("TooManyFunctions")

package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.db.PlaylistsDbInteractor
import com.example.playlistmaker.common.domain.db.TracksDbInteractor
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.common.presentation.models.TrackDurationTime
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.common.presentation.models.TrackInformationToTrackMapper
import com.example.playlistmaker.player.domain.MediaPlayerCallbackInterface
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.TrackStorageInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val trackStorageInteractor: TrackStorageInteractor,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val dbInteractor: TracksDbInteractor,
    private val playlistsInteractor: PlaylistsDbInteractor
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

    private val _bottomSheetState = MutableLiveData<PlayerBottomSheetState>()
    val bottomSheetState: LiveData<PlayerBottomSheetState> get() = _bottomSheetState

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
        _playerState.value = PlayerState.Loading
        lastState = PlayerState.Loading
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

    fun restoreState(): Triple<PlayerState?, TrackDurationTime, PlayerBottomSheetState?> {
        restoreLikeState()
        return Triple(lastState, lastTimerState, bottomSheetState.value)
    }

    private fun restoreLikeState() {
        viewModelScope.launch {
            initializedTrack?.trackId?.let {
                val value = dbInteractor.isTrackLiked(it)
                _likeState.postValue(value)
            }
        }
    }

    fun makeItPause() {
        if (_playerState.value == PlayerState.Play) {
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

    fun addCollection() {
        viewModelScope.launch {
            playlistsInteractor.giveMeAllPlaylists()
                .take(1)
                .collect {
                    _bottomSheetState.postValue(PlayerBottomSheetState.Shown(it))
                }
        }
    }

    fun hideCollection() {
        _bottomSheetState.postValue(PlayerBottomSheetState.Hidden)
    }

    fun handlePlaylistTap(playlist: PlaylistInformation) {
        viewModelScope.launch {
            val track = TrackInformationToTrackMapper().invoke(initializedTrack)
            val result = playlistsInteractor.addTrackToPlaylist(playlist.id.toString(), track)
            if (result) {
                _bottomSheetState.postValue(PlayerBottomSheetState.PlaylistAdded(playlist))
            } else {
                _bottomSheetState.postValue(PlayerBottomSheetState.PlaylistNotAdded(playlist))
            }
        }
    }

    fun handleNewPlaylistTap() {
        _bottomSheetState.postValue(PlayerBottomSheetState.NewPlaylist)
    }
}
