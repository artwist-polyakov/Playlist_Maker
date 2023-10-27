package com.example.playlistmaker.media.ui.view_model.models

import android.net.Uri

sealed class CreatePlaylistData {
    data class Title(val value: String) : CreatePlaylistData()
    data class ImageUri(val value: String) : CreatePlaylistData()
    data class Description(val value: Uri) : CreatePlaylistData()
}