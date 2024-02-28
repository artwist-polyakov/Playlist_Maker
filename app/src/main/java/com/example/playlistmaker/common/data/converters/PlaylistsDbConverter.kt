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

    fun map(playlists: List<PlaylistEntity>): List<PlaylistInformation> {
        return playlists.map(this::map)
    }

    fun map(playlistTrackReference: PlaylistTrackReference): Pair<String, Long> {
        return Pair(playlistTrackReference.playlistId, playlistTrackReference.trackId)
    }

    fun map(playlist: PlaylistInformation): PlaylistEntity {
        var image: String? = null
        playlist.image?.let {
            image = it.toString()
        }

        return with(playlist) {
            PlaylistEntity(
                id = id.toString(),
                name = name,
                description = description,
                imageUri = image,
                tracksCount = tracksCount,
                creationDate = creationDate,
                playlistDurationSeconds = durationInSeconds,
                wasDurationCalculated = wasDurationCalculated
            )
        }
    }

    fun map(playlist: PlaylistEntity): PlaylistInformation {
        var image: Uri? = null
        playlist.imageUri?.let {
            image = it.toUri()
        }

        return with(playlist) {
            PlaylistInformation(
                id = UUID.fromString(id),
                name = name,
                description = description,
                image = image,
                tracksCount = tracksCount,
                creationDate = creationDate,
                durationInSeconds = playlistDurationSeconds,
                wasDurationCalculated = wasDurationCalculated
            )
        }
    }
}
