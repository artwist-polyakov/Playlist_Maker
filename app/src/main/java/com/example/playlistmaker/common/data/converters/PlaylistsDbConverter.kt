package com.example.playlistmaker.common.data.converters

import android.net.Uri
import androidx.core.net.toUri
import com.example.playlistmaker.common.data.db.entity.PlaylistEntity
import com.example.playlistmaker.common.data.db.entity.PlaylistTrackReference
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.common.presentation.models.TrackInformation

class PlaylistsDbConverter {
    fun map(playlist: PlaylistInformation, track: TrackInformation): PlaylistTrackReference {
        return PlaylistTrackReference(
            playlistId = playlist.id,
            trackId = track.trackId,
            lastUpdate = System.currentTimeMillis()
        )
    }

    fun map(playlistTrackReference: PlaylistTrackReference): Pair<Long, Long> {
        return Pair(playlistTrackReference.playlistId, playlistTrackReference.trackId)
    }

    fun map(playlist: PlaylistInformation): PlaylistEntity {
        var image: String? = null
        playlist.image?.let {
            image = it.toString()
        }

        return PlaylistEntity(
            id = playlist.id,
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
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            image = image,
            tracksCount = playlist.tracksCount,
            creationDate = playlist.creationDate
        )
    }
}