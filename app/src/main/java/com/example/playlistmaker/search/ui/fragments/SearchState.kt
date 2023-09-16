package com.example.playlistmaker.search.ui.fragments

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.activity.ResponseState

sealed interface SearchState {

        object Loading : SearchState

        data class Content(
            val tracks: List<Track>
        ) : SearchState

        data class History(
            val tracks: List<Track>
        ) : SearchState

        data class Error(
            val responseState: ResponseState
        ) : SearchState

        data class Empty(
            val responseState: ResponseState
        ) : SearchState

        object Virgin : SearchState
}