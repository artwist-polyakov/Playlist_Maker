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
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: TracksDbInteractor,
    private val tracksStorage: TracksStorage
) : ViewModel() {
    private val state = MutableLiveData<FavoriteState>()
    fun observeState(): LiveData<FavoriteState> = state

    init {
        fillData()
    }

    private fun fillData() {
        state.postValue(FavoriteState.LOADING)
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
            state.postValue(FavoriteState.NOTHING_TO_SHOW)
        } else {
            state.postValue(FavoriteState.FAVORITES(favorites))
        }
    }

    fun saveTrackToHistory(track: TrackDto) {
        tracksStorage.pushTrackToHistory(track)
    }
}

