package com.example.playlistmaker.player.ui.view_model

import com.example.playlistmaker.common.presentation.models.PlaylistInformation

sealed class PlayerBottomSheetState {
    object Hidden : PlayerBottomSheetState()
    data class Shown(val playlists: List<PlaylistInformation>) : PlayerBottomSheetState()
    data class PlaylistAdded(val playlist: PlaylistInformation) : PlayerBottomSheetState()
    data class PlaylistNotAdded(val playlist: PlaylistInformation) : PlayerBottomSheetState()
    object NewPlaylist : PlayerBottomSheetState()
}
