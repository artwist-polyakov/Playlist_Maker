package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.models.Track

class PlaylistsViewModel : ViewModel() {
    val state = MutableLiveData<PlaylistState>(PlaylistState.NOTHING_TO_SHOW)
}

sealed class PlaylistState {
    object NOTHING_TO_SHOW : PlaylistState()
    object LOADING : PlaylistState()
    data class ERROR(val message: String) : PlaylistState()
    data class PLAYLISTS(val playlists: List<Playlist>) : PlaylistState()
}

class Playlist(val name: String, val tracks: List<Track>)