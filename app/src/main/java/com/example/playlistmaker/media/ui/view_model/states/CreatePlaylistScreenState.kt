package com.example.playlistmaker.media.ui.view_model.states

sealed class CreatePlaylistScreenState {
    object ReadyToSave: CreatePlaylistScreenState()
    object NotReadyToSave: CreatePlaylistScreenState()
    object ShowPopupConfirmation: CreatePlaylistScreenState()
    object GoodBye: CreatePlaylistScreenState()
    object BasicState: CreatePlaylistScreenState()
}
