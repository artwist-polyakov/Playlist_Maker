package com.example.playlistmaker.player.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.presentation.models.TrackDurationTime
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.player.domain.MediaPlayerCallbackInterface
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.TrackStorageInteractor
import com.example.playlistmaker.player.domain.MediaPlayerInterface
import com.example.playlistmaker.search.ui.activity.SearchState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerViewModel : ViewModel(), MediaPlayerCallbackInterface, KoinComponent {

    // LiveData для состояния проигрывателя
    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState
    private var lastState: PlayerState? = null

    private val _timerState = MutableLiveData<TrackDurationTime>()
    val timerState: LiveData<TrackDurationTime> get() = _timerState
    private val lastTimerState: TrackDurationTime = TrackDurationTime(0)

    // MediaPlayer
    private val trackStorageInteractor: TrackStorageInteractor by inject()
    private val mediaPlayerInteractor: MediaPlayerInteractor by inject()
    private var initializedTrack = trackStorageInteractor.giveMeLastTrack()

    // Функции для управления проигрыванием
    fun playPause() {
        mediaPlayerInteractor.playPauseSwitcher()
    }

    fun resetPlayer() {
        Log.d("currentButtonState", "i want to destroy player from view model")
        mediaPlayerInteractor.destroyPlayer()
    }

    override fun onMediaPlayerReady() {
        val hash = this.hashCode()
        Log.d("currentButtonState", "callbackAccepted $hash")
        _playerState.value = PlayerState.Ready
        lastState = PlayerState.Ready
        Log.d("currentButtonState", "Changing LiveData layerState.Ready $hash")
    }

    override fun onMediaPlayerTimeUpdate(time: TrackDurationTime) {
        _timerState.value = time
//        lastState = PlayerState.TimeUpdate(time)
    }

    override fun onMediaPlayerPause() {
        _playerState.value = PlayerState.Pause
        lastState = PlayerState.Pause
    }

    override fun onMediaPlayerPlay() {
        _playerState.value = PlayerState.Play
        lastState = PlayerState.Play
    }

    fun initializePlayer() {
        val hash = this.hashCode()
        Log.d("currentButtonState", "i ($hash)  want to init player //VIEWMODEL")
        mediaPlayerInteractor.setCallback(this)
         giveCurrentTrack()?.let {
             mediaPlayerInteractor.initialize(it)
         }
    }

    fun giveCurrentTrack(): TrackInformation? {
        return initializedTrack
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.destroyPlayer()
    }

    fun restoreState(): Pair<PlayerState?, TrackDurationTime> {
        return Pair(lastState, lastTimerState)
    }

}