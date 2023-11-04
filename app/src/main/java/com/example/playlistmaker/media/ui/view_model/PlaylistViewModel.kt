package com.example.playlistmaker.media.ui.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.db.PlaylistsDbInteractor
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.common.presentation.models.TrackToTrackDtoMapper
import com.example.playlistmaker.media.ui.view_model.states.SinglePlaylistScreenInteraction
import com.example.playlistmaker.media.ui.view_model.states.SinglePlaylistScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.storage.TracksStorage
import kotlinx.coroutines.launch

class PlaylistViewModel (
    val playlistId: String,
    val playlistsInteractor: PlaylistsDbInteractor,
    private val tracksStorage: TracksStorage
): ViewModel() {

    private val _state = MutableLiveData<SinglePlaylistScreenState>()
    val state: MutableLiveData<SinglePlaylistScreenState> get() = _state
    private val _playlistState = MutableLiveData<ArrayList<Track>>()
    val playlistState: MutableLiveData<ArrayList<Track>> get() = _playlistState

    var dataLoaded: Boolean = false

    init {
        fillData()
    }

    private fun fillData() {
        _state.postValue(SinglePlaylistScreenState.Loading)
        viewModelScope.launch {
                processResult(playlistsInteractor.getPlaylist(playlistId))
        }
        viewModelScope.launch {
            playlistsInteractor
                .giveMeTracksFromPlaylist(playlistId)
                .collect() {
                    Log.d("PlaylistViewModel", "fillData $it")
                    _playlistState.postValue(ArrayList(it))
                    dataLoaded = true
                }
        }
    }

    private fun processResult(playlist: PlaylistInformation) {
        _state.postValue(SinglePlaylistScreenState.Success(playlist))
    }

    fun handleInteraction(interaction: SinglePlaylistScreenInteraction) {
        when (interaction) {
            is SinglePlaylistScreenInteraction.SharePlaylist -> {
                if (_playlistState.value?.isEmpty() != false) {
                    _state.postValue(SinglePlaylistScreenState.ShowMessageEmptyList)
                    return
                } else {
                    _state.postValue(SinglePlaylistScreenState.SharePlaylistInitiated)
                }

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

            is SinglePlaylistScreenInteraction.TrackClicked -> {
                val trackDto = TrackToTrackDtoMapper().invoke(interaction.track)
                tracksStorage.pushTrackToHistory(trackDto)
                tracksStorage.saveHistory()
            }

            is SinglePlaylistScreenInteraction.toBasicState -> {
                _state.postValue(SinglePlaylistScreenState.Basic)
            }
        }
    }

    fun checkDataLoaded() {
        if (dataLoaded) {
            _playlistState.value.let{
                _playlistState.postValue(it)
                dataLoaded = false
            }
        }
    }

}