package com.example.playlistmaker.media.ui.view_model.states

import com.example.playlistmaker.common.presentation.models.PlaylistInformation

sealed class PlaylistsScreenState {
    data class Content(val content: List<PlaylistInformation>) : PlaylistsScreenState()
    object Empty : PlaylistsScreenState()
}
