package com.example.playlistmaker.player.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.presentation.models.TrackDurationTime
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.player.data.MediaPlayerImpl
import com.example.playlistmaker.player.domain.MediaPlayerCallbackInterface
import com.example.playlistmaker.player.domain.TrackStorageInteractor
import com.example.playlistmaker.player.domain.api.MediaPlayerInterface
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PlayerViewModel : ViewModel(), MediaPlayerCallbackInterface, KoinComponent {

    // LiveData для состояния проигрывателя
    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    // MediaPlayer
    private val trackStorageInteractor: TrackStorageInteractor by inject()
    private var initializedTrack = trackStorageInteractor.giveMeLastTrack()
    private val mediaPlayer: MediaPlayerInterface by inject()

//    var currentTrack: TrackInformation? = null
//        set(value) {
//            field = value
//            // Какие-то действия при установке нового трека (если нужны)
//        }
    // Функции для управления проигрыванием
    fun playPause() {
        mediaPlayer.playPauseSwitcher()
    }

    fun resetPlayer() {
        Log.d("currentButtonState", "i want to destroy player from view model")
        mediaPlayer.destroyPlayer()
    }

    fun changeTrack(track: TrackInformation) {
        mediaPlayer.destroyPlayer()
//        currentTrack = track
    }

    fun startPlayer() {
        mediaPlayer.startPlayer()
    }

    fun pausePlayer() {
        mediaPlayer.pausePlayer()
    }

    fun getTrackPosition(): Int {
        return mediaPlayer.getTrackPosition()
    }

    fun setTrackPosition(position: Int) {
        mediaPlayer.setTrackPosition(position)
    }

    override fun onMediaPlayerReady() {
        Log.d("currentButtonState", "callbackAccepted")
        _playerState.value = PlayerState.Ready
        Log.d("currentButtonState", "Changing LiveData layerState.Ready")
    }

    override fun onMediaPlayerTimeUpdate(time: TrackDurationTime) {
        _playerState.value = PlayerState.TimeUpdate(time)
    }

    override fun onMediaPlayerPause() {
        _playerState.value = PlayerState.Pause
    }

    override fun onMediaPlayerPlay() {
        _playerState.value = PlayerState.Play
    }

    fun initializePlayer() {
        Log.d("currentButtonState", "i want to init player //VIEWMODEL")
        mediaPlayer.forceInit()
    }

    fun giveCurrentTrack(): TrackInformation? {
        return initializedTrack
    }


}