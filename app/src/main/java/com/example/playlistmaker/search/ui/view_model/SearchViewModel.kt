package com.example.playlistmaker.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.common.presentation.models.TrackDtoToTrackMapper
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.storage.TracksStorage
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.activity.ResponseState
import com.example.playlistmaker.search.ui.activity.SearchState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class SearchViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val tracksInteractor: TracksInteractor by inject()
    private val tracksStorage: TracksStorage by inject()
    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>()

    private val _backButtonPressed = MutableLiveData<Unit>()
    val backButtonPressed: LiveData<Unit> get() = _backButtonPressed

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
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
            tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundMovies: List<Track>?, errorMessage: String?) {
                    val tracks = mutableListOf<Track>()
                    if (foundMovies != null) {
                        tracks.addAll(foundMovies)
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
            })
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
        renderState(SearchState.Virgin)
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

    fun onBackButtonPressed() {
        _backButtonPressed.value = Unit
    }

    fun onClearButtonPressed() {
        _clearButtonPressed.value = Unit
    }

    fun clearHistoryAndHide() {
        clearHistory()
        renderState(SearchState.Virgin) // это будет прятать историю и результаты поиска
    }
}