package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.db.TracksDbInteractor
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.storage.TracksStorage
import com.example.playlistmaker.search.ui.view_model.FavoriteState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: TracksDbInteractor,
    private val tracksStorage: TracksStorage
) : ViewModel() {
    private val _state = MutableStateFlow<FavoriteState>(FavoriteState.LOADING)
    val state: StateFlow<FavoriteState> = _state

    init {
        fillData()
    }

    private fun fillData() {
        _state.value = FavoriteState.LOADING
        viewModelScope.launch {
            favoritesInteractor
                .allLikedTracks()
                .collect { favorites ->
                    processResult(favorites)
                }
        }
    }

    private fun processResult(favorites: List<Track>) {
        if (favorites.isEmpty()) {
            _state.value = FavoriteState.NOTHING_TO_SHOW
        } else {
            _state.value = FavoriteState.FAVORITES(favorites)
        }
    }

    fun saveTrackToHistory(track: TrackDto) {
        tracksStorage.pushTrackToHistory(track)
    }
}

