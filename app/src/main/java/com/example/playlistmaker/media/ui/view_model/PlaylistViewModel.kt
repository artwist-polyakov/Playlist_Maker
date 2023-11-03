package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.domain.db.PlaylistsDbInteractor

class PlaylistViewModel (
    val playlistId: String,
    val playlistsInteractor: PlaylistsDbInteractor
): ViewModel() {
}