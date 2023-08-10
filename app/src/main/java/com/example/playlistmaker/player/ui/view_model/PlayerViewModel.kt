package com.example.playlistmaker.player.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.common.presentation.models.TrackDurationTime
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.player.data.MediaPlayerImpl
import com.example.playlistmaker.player.domain.MediaPlayerCallbackInterface

class PlayerViewModel(var withTrack: TrackInformation) : ViewModel(), MediaPlayerCallbackInterface {
    companion object {
        fun getViewModelFactory(withTrack: TrackInformation): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlayerViewModel(withTrack) as T
            }
        }
    }
    // LiveData для состояния проигрывателя
    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> get() = _playerState

    // MediaPlayer
    private val mediaPlayer = MediaPlayerImpl(callback = this, withTrack = withTrack)

    var currentTrack: TrackInformation? = null
        set(value) {
            field = value
            // Какие-то действия при установке нового трека (если нужны)
        }
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
        mediaPlayer.withTrack = track
        currentTrack = track
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
        _playerState.value = PlayerState.Ready
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


}