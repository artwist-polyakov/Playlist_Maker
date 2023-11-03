package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.db.PlaylistsDbInteractor
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.media.ui.view_model.states.PlaylistsScreenInteraction
import com.example.playlistmaker.media.ui.view_model.states.PlaylistsScreenState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsDb: PlaylistsDbInteractor,
) : ViewModel() {
    private val _state = MutableLiveData<PlaylistsScreenState>()
    val state: LiveData<PlaylistsScreenState> get() = _state

    init {
//        _state.postValue(PlaylistsScreenState.Empty)
        fillData()
    }

    private fun fillData() {
        viewModelScope.launch {
            playlistsDb
                .giveMeAllPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<PlaylistInformation>) {
        if (playlists.isEmpty()) {
            _state.postValue(PlaylistsScreenState.Empty)
        } else {
            _state.postValue(PlaylistsScreenState.Content(playlists))
        }
    }
}

