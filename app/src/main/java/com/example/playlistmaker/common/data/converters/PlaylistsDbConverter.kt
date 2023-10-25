package com.example.playlistmaker.common.data.converters

import android.net.Uri
import androidx.core.net.toUri
import com.example.playlistmaker.common.data.db.entity.PlaylistEntity
import com.example.playlistmaker.common.data.db.entity.PlaylistTrackReference
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import java.util.UUID

class PlaylistsDbConverter {
    fun map(playlistId: String, trackId: Long): PlaylistTrackReference {
        return PlaylistTrackReference(
            playlistId = playlistId,
            trackId = trackId,
            lastUpdate = System.currentTimeMillis()
        )
    }

    fun map(playlistTrackReference: PlaylistTrackReference): Pair<String, Long> {
        return Pair(playlistTrackReference.playlistId, playlistTrackReference.trackId)
    }

    fun map(playlist: PlaylistInformation): PlaylistEntity {
        var image: String? = null
        playlist.image?.let {
            image = it.toString()
        }

        return PlaylistEntity(
            id = playlist.id.toString(),
            name = playlist.name,
            description = playlist.description,
            imageUri = image,
            tracksCount = playlist.tracksCount,
            creationDate = playlist.creationDate
        )
    }

    fun map(playlist: PlaylistEntity): PlaylistInformation {
        var image: Uri? = null
        playlist.imageUri?.let {
            image = it.toUri()
        }

        return PlaylistInformation(
            id = UUID.fromString(playlist.id),
            name = playlist.name,
            description = playlist.description,
            image = image,
            tracksCount = playlist.tracksCount,
            creationDate = playlist.creationDate
        )
    }
}