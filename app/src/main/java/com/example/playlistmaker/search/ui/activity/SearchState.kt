package com.example.playlistmaker.search.ui.activity

import com.example.playlistmaker.search.models.Track

sealed interface SearchState {

        object Loading : SearchState

        data class Content(
            val tracks: List<Track>
        ) : SearchState

        data class Error(
            val responseState: ResponseState
        ) : SearchState

        data class Empty(
            val responseState: ResponseState
        ) : SearchState
}