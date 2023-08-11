package com.example.playlistmaker.search.ui.view_model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.common.presentation.mappers.TrackDtoToTrackMappers
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.storage.TracksStorage
import com.example.playlistmaker.search.data.storage.TracksStorageImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.activity.ResponseState
import com.example.playlistmaker.search.ui.activity.SearchState

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val tracksInteractor = Creator.provideTracksInteractor(getApplication())
    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>()

    private val _backButtonPressed = MutableLiveData<Unit>()
    val backButtonPressed: LiveData<Unit> get() = _backButtonPressed

    private val _clearButtonPressed = MutableLiveData<Unit>()
    val clearButtonPressed: LiveData<Unit> get() = _clearButtonPressed


    private val sharedPreferences: SharedPreferences = getApplication<Application>().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    private val tracksStorage: TracksStorage = TracksStorageImpl(sharedPreferences)

    private var latestSearchText: String? = null

    private var lastState: SearchState? = null

    private val mediatorStateLiveData = MediatorLiveData<SearchState>().also { liveData ->
        liveData.addSource(stateLiveData) { searchState ->
            Log.d("SearchViewModelState", "searchState: $searchState")
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
            renderState(lastState!!)
        } else {
            loadHistoryTracks()
        }
    }

    fun loadHistoryTracks() {
        val historyTracksDto = tracksStorage.takeHistory(reverse = true)
        val historyTracks = historyTracksDto.map { TrackDtoToTrackMappers().invoke(it) }
        renderState(SearchState.Virgin)
        if (historyTracks.isNotEmpty()) {
            renderState(SearchState.History(historyTracks))
            Log.d("SearchViewModel", "history tracks $historyTracks")
        } else {
            Log.d("SearchViewModel", "No history tracks")
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