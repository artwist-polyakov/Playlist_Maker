package com.example.playlistmaker.media.ui.view_model.states

import com.example.playlistmaker.common.presentation.models.PlaylistInformation

sealed class PlaylistsScreenInteraction {
    object CreateButtonPressed: PlaylistsScreenInteraction()
    data class PlaylistClicked (val content: PlaylistInformation): PlaylistsScreenInteraction()
    object newPlaylistButtonPressed: PlaylistsScreenInteraction()
}
