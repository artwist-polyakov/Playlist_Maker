package com.example.playlistmaker.media.ui.view_model.states

import android.net.Uri

data class PlaylistInputData (
    val title: String = "",
    val description: String = "",
    val image: Uri? = null
)