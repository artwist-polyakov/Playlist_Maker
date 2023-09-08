package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.models.Track

class FavoritesViewModel : ViewModel() {
    val state = MutableLiveData<FavoriteState>(FavoriteState.NOTHING_TO_SHOW)
}

sealed class FavoriteState {
    object NOTHING_TO_SHOW : FavoriteState()
    object LOADING : FavoriteState()
    data class ERROR(val message: String) : FavoriteState()
    data class FAVORITES(val favorites: List<Track>) : FavoriteState()
}