package com.example.playlistmaker.media.ui.view_model.models

import android.net.Uri
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import java.util.UUID

data class PlaylistInputData (
    val title: String = "",
    val description: String = "",
    val image: Uri? = null
) {
    fun isEmpty(): Boolean {
        return title.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

    fun isDataEntered(): Boolean {
        return title.isNotEmpty() || description.isNotEmpty() || image != null
    }

    fun mapToPlaylistInformation(): PlaylistInformation {
        return PlaylistInformation(
            id = UUID.randomUUID(),
            name = title,
            description = description,
            image = image,
            tracksCount = 0,
            creationDate = System.currentTimeMillis()
        )
    }
}