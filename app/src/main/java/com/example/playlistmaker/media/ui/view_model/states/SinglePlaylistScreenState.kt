package com.example.playlistmaker.media.ui.view_model.states

import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.search.domain.models.Track

sealed class SinglePlaylistScreenState {
    object Basic : SinglePlaylistScreenState()
    object Loading : SinglePlaylistScreenState()
    object Error : SinglePlaylistScreenState()
    object ShowMessageEmptyList : SinglePlaylistScreenState()
    data class Success(val playlist: PlaylistInformation) : SinglePlaylistScreenState()
    data class  SharePlaylistInitiated (val playlist: PlaylistInformation?, val tracks: ArrayList<Track>?) : SinglePlaylistScreenState()
    object GoBack : SinglePlaylistScreenState()
    object ShownOptions : SinglePlaylistScreenState()
    data class ConfirmDelete(val playlist: PlaylistInformation) : SinglePlaylistScreenState()
    object CancelDelete : SinglePlaylistScreenState()
    object DeleteSuccess : SinglePlaylistScreenState()
    data class ConfirmTrackDelete(val track: Track) : SinglePlaylistScreenState()
    object SuccessTrackDelete : SinglePlaylistScreenState()
}
