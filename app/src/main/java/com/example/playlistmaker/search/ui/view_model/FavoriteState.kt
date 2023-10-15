package com.example.playlistmaker.search.ui.view_model

import com.example.playlistmaker.search.domain.models.Track

sealed class FavoriteState {
    object NOTHING_TO_SHOW : FavoriteState()
    object LOADING : FavoriteState()
    data class FAVORITES(val favorites: List<Track>) : FavoriteState()
}
