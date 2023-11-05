package com.example.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.presentation.models.TrackDtoToTrackMapper
import com.example.playlistmaker.common.utils.debounce
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.storage.TracksStorage
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.fragments.ResponseState
import com.example.playlistmaker.search.ui.fragments.SearchState
import kotlinx.coroutines.launch

class SearchViewModel(
    private val application: Application,
    private val tracksInteractor: TracksInteractor,
    private val tracksStorage: TracksStorage
) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val musicSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { text ->
        searchRequest(text)
    }

    private val stateLiveData = MutableLiveData<SearchState>()

    private val _clearButtonPressed = MutableLiveData<Unit>()
    val clearButtonPressed: LiveData<Unit> get() = _clearButtonPressed

    private var latestSearchText: String? = null
    private var lastState: SearchState? = null

    private val mediatorStateLiveData = MediatorLiveData<SearchState>().also { liveData ->
        liveData.addSource(stateLiveData) { searchState ->
            liveData.value = when (searchState) {
                is SearchState.Content -> SearchState.Content(searchState.tracks)
                is SearchState.Empty -> searchState
                is SearchState.Error -> searchState
                is SearchState.Loading -> searchState
                is SearchState.History -> SearchState.History(searchState.tracks)
                is SearchState.Virgin -> searchState
            }
        }
    }

    fun observeState(): LiveData<SearchState> = mediatorStateLiveData

    override fun onCleared() {
        super.onCleared()
        tracksStorage.saveHistory()
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            musicSearchDebounce(changedText)
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
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks.map { dto ->
                TrackDtoToTrackMapper().invoke(dto)
            })
        }

        when {
            errorMessage != null -> {
                renderState(
                    SearchState.Error(
                        responseState = ResponseState.ERROR,
                    )
                )
            }

            tracks.isEmpty() -> {
                renderState(
                    SearchState.Empty(
                        responseState = ResponseState.NOTHING_FOUND,
                    )
                )
            }

            else -> {
                renderState(
                    SearchState.Content(
                        tracks = tracks,
                    )
                )
            }
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
        lastState = state
    }

    fun restoreLastState() {
        if (lastState != null) {
            if (lastState is SearchState.History) {
                val tracks = tracksStorage.takeHistory(reverse = true).map {
                    TrackDtoToTrackMapper().invoke(it)
                }
                renderState(SearchState.History(tracks))
            } else {
                renderState(lastState!!)
            }
        } else {
            loadHistoryTracks()
        }
    }

    fun loadHistoryTracks() {
        val historyTracksDto = tracksStorage.takeHistory(reverse = true)
        val historyTracks = historyTracksDto.map { TrackDtoToTrackMapper().invoke(it) }
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

    fun clearHistory() {
        tracksStorage.clearHistory()
    }

    fun onClearButtonPressed() {
        _clearButtonPressed.value = Unit
    }

    fun clearHistoryAndHide() {
        clearHistory()
        renderState(SearchState.Virgin) // это будет прятать историю и результаты поиска
    }
}