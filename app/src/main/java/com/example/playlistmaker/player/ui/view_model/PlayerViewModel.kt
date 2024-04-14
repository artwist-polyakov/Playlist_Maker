package com.example.playlistmaker.player.ui.view_model

import android.util.Log
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
import com.example.playlistmaker.player.domain.MusicServiceInteractor
import com.example.playlistmaker.player.domain.TrackStorageInteractor
import com.example.playlistmaker.player.service.PlayerServiceState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val trackStorageInteractor: TrackStorageInteractor,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val dbInteractor: TracksDbInteractor,
    private val playlistsInteractor: PlaylistsDbInteractor,
    private val musicServiceInteractor: MusicServiceInteractor
) : ViewModel(), MediaPlayerCallbackInterface {


    private var timerJob: Job? = null

    // LiveData для состояния проигрывателя
    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState
    private var lastState: PlayerState? = null

    private val _likeState = MutableLiveData<Boolean>()
    val likeState: LiveData<Boolean> get() = _likeState

    private var hasServicePermission = false

    private val _bottomSheetState = MutableLiveData<PlayerBottomSheetState>()
    val bottomSheetState: LiveData<PlayerBottomSheetState> get() = _bottomSheetState

    private val _timerState = MutableLiveData<TrackDurationTime>()
    val timerState: LiveData<TrackDurationTime> get() = _timerState
    private val lastTimerState: TrackDurationTime = TrackDurationTime(0)

    // MediaPlayer
    private var initializedTrack = trackStorageInteractor.giveMeLastTrack()

    fun setPermissionsState(state: Boolean) {
        hasServicePermission = state
        if (state) {
            viewModelScope.launch {
                musicServiceInteractor.configureAndLaunchService().collect {
                    when (it) {
                        is PlayerServiceState.Playing -> _playerState.postValue(PlayerState.Play)

                        is PlayerServiceState.Paused -> _playerState.postValue(PlayerState.Pause)
                        is PlayerServiceState.Prepared -> _playerState.postValue(PlayerState.Ready)
                        is PlayerServiceState.Default -> _playerState.postValue(PlayerState.Loading)
                    }
                    _timerState.postValue(TrackDurationTime(it.progress))
                    lastState = _playerState.value

                }
            }
        } else {
            Log.d("PlayerViewModel", "No permission for service")
        }
    }

    // Функции для управления проигрыванием
    fun playPause() {
        if (!hasServicePermission) {
            return
        }
        if (_playerState.value == PlayerState.Play) {
            musicServiceInteractor.pause()
            return
        }
        musicServiceInteractor.play()
    }

    fun resetPlayer() {
        musicServiceInteractor.unBindService()
    }

    override fun onMediaPlayerReady() {
        _playerState.value = PlayerState.Ready
        lastState = PlayerState.Ready
//        _timerState.postValue(TrackDurationTime(0))
    }

    override fun onMediaPlayerPause() {
        _playerState.value = PlayerState.Pause
        lastState = PlayerState.Pause
//        timerJob?.cancel()
    }

    override fun onMediaPlayerPlay() {
        _playerState.value = PlayerState.Play
        lastState = PlayerState.Play
//        startTimer()
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

    fun unBindService() {
        musicServiceInteractor.unBindService()
    }

    companion object {
        const val UPDATE_FREQUENCY = 250L
    }
}