package com.example.playlistmaker.media.ui.view_model.states

import com.example.playlistmaker.media.ui.view_model.models.CreatePlaylistData

sealed class CreatePlaylistScreenInteraction {
    object backButtonPressed: CreatePlaylistScreenInteraction()
    object saveButtonPressed: CreatePlaylistScreenInteraction()
    data class dataFilled(val content: CreatePlaylistData )
}
