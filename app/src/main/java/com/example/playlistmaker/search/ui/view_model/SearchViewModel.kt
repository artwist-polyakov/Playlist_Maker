package com.example.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.presentation.models.TrackDtoToTrackMapper
import com.example.playlistmaker.common.presentation.debounce
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.storage.TracksStorage
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.fragments.ResponseState
import com.example.playlistmaker.search.ui.fragments.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val application: Application,
    private val tracksInteractor: TracksInteractor,
    private val tracksStorage: TracksStorage
) : AndroidViewModel(application) {
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Virgin)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private var searchJob: Job? = null
    private var latestSearchText: String? = null

    fun updateSearchText(newText: String) {
        _searchText.value = newText
        searchDebounce(newText)
    }

    fun searchDebounce(changedText: String) {
        searchJob?.cancel()
        latestSearchText = changedText
        if (changedText.isEmpty()) {
            loadHistoryTracks()
        } else {
            searchJob = viewModelScope.launch {
                delay(2000) // 2 seconds delay
                searchRequest(changedText)
            }
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            _searchState.value = SearchState.Loading
            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<TrackDto>?, errorMessage: String?) {
        val tracks = foundTracks?.map { TrackDtoToTrackMapper().invoke(it) } ?: emptyList()

        when {
            errorMessage != null -> _searchState.value = SearchState.Error(ResponseState.ERROR)
            tracks.isEmpty() -> _searchState.value = SearchState.Empty(ResponseState.NOTHING_FOUND)
            else -> _searchState.value = SearchState.Content(tracks)
        }
    }

    private fun loadHistoryTracks() {
        viewModelScope.launch {
            val historyTracks = tracksStorage.takeHistory(reverse = true)
            if (historyTracks.isNotEmpty()) {
                _searchState.value = SearchState.History(tracks = historyTracks.map { it ->
                    TrackDtoToTrackMapper().invoke(it)
                })
            } else {
                _searchState.value = SearchState.Virgin
            }
        }
    }

    fun saveTrackToHistory(track: TrackDto) {
        tracksStorage.pushTrackToHistory(track)
        tracksStorage.saveHistory()
    }

    fun clearHistoryAndHide() {
        tracksStorage.clearHistory()
        _searchState.value = SearchState.Virgin
    }

    fun onClearButtonPressed() {
        _searchText.value = ""
        loadHistoryTracks()
    }

    fun retrySearch() {
        latestSearchText?.let { searchRequest(it) }
    }

    fun restoreLastState() {
        latestSearchText?.let {
            if (it.isNotEmpty()) {
                searchRequest(it)
            } else {
                loadHistoryTracks()
            }
        } ?: loadHistoryTracks()
    }
}