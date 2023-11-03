package com.example.playlistmaker.media.ui.view_model.states

import com.example.playlistmaker.common.presentation.models.PlaylistInformation

sealed class SinglePlaylistScreenState {
    object Loading : SinglePlaylistScreenState()
    object Error : SinglePlaylistScreenState()
    object Empty : SinglePlaylistScreenState()
    data class Success(val playlist: PlaylistInformation) : SinglePlaylistScreenState()
    object SharePlaylistInitiated : SinglePlaylistScreenState()

    object GoBack : SinglePlaylistScreenState()
}
