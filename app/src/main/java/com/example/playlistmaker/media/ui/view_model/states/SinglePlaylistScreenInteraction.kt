package com.example.playlistmaker.media.ui.view_model.states

import com.example.playlistmaker.search.domain.models.Track

sealed class SinglePlaylistScreenInteraction {
    object toBasicState : SinglePlaylistScreenInteraction()
    object SharePlaylist : SinglePlaylistScreenInteraction()
    object DeletePlaylist : SinglePlaylistScreenInteraction()
    object TappedBackButton : SinglePlaylistScreenInteraction()
    object OptionsClicked : SinglePlaylistScreenInteraction()
    object OptionsDismissed : SinglePlaylistScreenInteraction()
    data class TrackClicked(val track: Track) : SinglePlaylistScreenInteraction()
    data class sendMessage(val message: String) : SinglePlaylistScreenInteraction()
    object confirmDelete : SinglePlaylistScreenInteraction()
    object cancelDelete : SinglePlaylistScreenInteraction()
    data class longTrackTap(val track: Track) : SinglePlaylistScreenInteraction()
    data class confirmDeleteTrack(val track: Track) : SinglePlaylistScreenInteraction()
    object editPlaylist : SinglePlaylistScreenInteraction()
}
