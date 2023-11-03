package com.example.playlistmaker.media.ui.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.db.PlaylistsDbInteractor
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.media.ui.view_model.states.SinglePlaylistScreenInteraction
import com.example.playlistmaker.media.ui.view_model.states.SinglePlaylistScreenState
import kotlinx.coroutines.launch

class PlaylistViewModel (
    val playlistId: String,
    val playlistsInteractor: PlaylistsDbInteractor
): ViewModel() {

    private val _state = MutableLiveData<SinglePlaylistScreenState>()
    val state: MutableLiveData<SinglePlaylistScreenState> get() = _state

    init {
        fillData()
    }

    private fun fillData() {
        _state.postValue(SinglePlaylistScreenState.Loading)
        viewModelScope.launch {
                processResult(playlistsInteractor.getPlaylist(playlistId))
        }
    }

    private fun processResult(playlist: PlaylistInformation) {
        _state.postValue(SinglePlaylistScreenState.Success(playlist))
    }

    fun handleInteraction(interaction: SinglePlaylistScreenInteraction) {
        when (interaction) {
            is SinglePlaylistScreenInteraction.SharePlaylist -> {
                _state.postValue(SinglePlaylistScreenState.SharePlaylistInitiated)
            }
            is SinglePlaylistScreenInteraction.DeletePlaylist -> {
                Log.d("PlaylistViewModel", "DeletePlaylist")
            }
            is SinglePlaylistScreenInteraction.TappedBackButton -> {
                _state.postValue(SinglePlaylistScreenState.GoBack)
            }
            is SinglePlaylistScreenInteraction.OptionsClicked -> {
                Log.d("PlaylistViewModel", "OptionsClicked")
            }
            is SinglePlaylistScreenInteraction.OptionsDismissed -> {
                Log.d("PlaylistViewModel", "OptionsDismissed")
            }
        }
    }

}