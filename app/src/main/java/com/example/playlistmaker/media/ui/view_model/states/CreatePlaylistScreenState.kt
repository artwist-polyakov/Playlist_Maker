package com.example.playlistmaker.media.ui.view_model.states

import android.net.Uri

sealed class CreatePlaylistScreenState {
    object ReadyToSave : CreatePlaylistScreenState()
    object NotReadyToSave : CreatePlaylistScreenState()
    object ShowPopupConfirmation : CreatePlaylistScreenState()
    object GoodBye : CreatePlaylistScreenState()
    object BasicState : CreatePlaylistScreenState()
    data class SuccessState(val name: String) : CreatePlaylistScreenState()
    data class ReadyToEdit(
        val name: String,
        val description: String,
        val image: Uri?
    ) : CreatePlaylistScreenState()
}
