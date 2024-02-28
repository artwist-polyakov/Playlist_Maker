package com.example.playlistmaker.media.ui.view_model.states

import com.example.playlistmaker.media.ui.view_model.models.CreatePlaylistData

sealed class CreatePlaylistScreenInteraction {
    object BackButtonPressed : CreatePlaylistScreenInteraction()
    object SaveButtonPressed : CreatePlaylistScreenInteraction()
    data class DataFilled(val content: CreatePlaylistData) : CreatePlaylistScreenInteraction()
}
