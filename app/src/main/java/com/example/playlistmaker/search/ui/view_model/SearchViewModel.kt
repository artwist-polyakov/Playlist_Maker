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

    private val stateLiveData = MutableLiveData<SearchState>()
    private val _clearButtonPressed = MutableLiveData<Unit>()
    val clearButtonPressed: LiveData<Unit> get() = _clearButtonPressed

    private var latestSearchText: String? = null
    private var lastState: SearchState? = null

    private val searchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { text ->
            searchRequest(text)
        }

    fun observeState(): LiveData<SearchState> = stateLiveData

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            searchDebounce(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
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
            errorMessage != null -> renderState(SearchState.Error(ResponseState.ERROR))
            tracks.isEmpty() -> renderState(SearchState.Empty(ResponseState.NOTHING_FOUND))
            else -> renderState(SearchState.Content(tracks))
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
        lastState = state
    }

    fun restoreLastState() {
        lastState?.let { renderState(it) } ?: loadHistoryTracks()
    }

    fun loadHistoryTracks() {
        val historyTracks =
            tracksStorage.takeHistory(reverse = true).map { TrackDtoToTrackMapper().invoke(it) }
        if (historyTracks.isNotEmpty()) {
            renderState(SearchState.History(historyTracks))
        } else {
            renderState(SearchState.Virgin)
        }
    }

    fun saveTrackToHistory(track: TrackDto) {
        tracksStorage.pushTrackToHistory(track)
        tracksStorage.saveHistory()
    }

    fun clearHistoryAndHide() {
        tracksStorage.clearHistory()
        renderState(SearchState.Virgin)
    }

    fun onClearButtonPressed() {
        _clearButtonPressed.value = Unit
    }

    fun retrySearch() {
        latestSearchText?.let { searchRequest(it) }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 300L
    }
}