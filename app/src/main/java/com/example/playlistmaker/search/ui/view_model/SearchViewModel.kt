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
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.models.SingleLiveEvent
import com.example.playlistmaker.common.presentation.mappers.TrackDtoToTrackMappers
import com.example.playlistmaker.creator.search.Creator
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.storage.TracksStorage
import com.example.playlistmaker.search.data.storage.TracksStorageImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.models.Track
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

    private val mediatorStateLiveData = MediatorLiveData<SearchState>().also { liveData ->
        liveData.addSource(stateLiveData) { searchState ->
            liveData.value = when (searchState) {
                // 2
                is SearchState.Content -> SearchState.Content(searchState.tracks)
                is SearchState.Empty -> searchState
                is SearchState.Error -> searchState
                is SearchState.Loading -> searchState
                is SearchState.History -> SearchState.History(searchState.tracks)
            }
        }
    }
    fun observeState(): LiveData<SearchState> = mediatorStateLiveData

    override fun onCleared() {
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
    }

    fun loadHistoryTracks() {
        val historyTracks = tracksStorage.takeHistory(reverse = true)
        if (historyTracks.isNotEmpty()) {
            renderState(SearchState.History(
                historyTracks.map { it ->
                    TrackDtoToTrackMappers().invoke(it)
                }
            ))
            Log.d("SearchViewModel", "history tracks $historyTracks")
        } else {
            // Если у вас нет сохраненных треков, вы можете решить, что делать здесь.
            Log.d("SearchViewModel", "No history tracks")
            renderState(SearchState.Empty(ResponseState.NOTHING_FOUND))
        }
    }

    fun saveTrackToHistory(track: TrackDto) {
        tracksStorage.pushTrackToHistory(track)
    }

    fun clearHistory() {
        tracksStorage.clearHistory()
        // Вызовите здесь обновление интерфейса, если это необходимо
    }

    fun onBackButtonPressed() {
        _backButtonPressed.value = Unit
    }

    fun onClearButtonPressed() {
        _clearButtonPressed.value = Unit
    }


}