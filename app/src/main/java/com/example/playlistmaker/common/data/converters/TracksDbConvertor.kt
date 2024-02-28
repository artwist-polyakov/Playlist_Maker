package com.example.playlistmaker.common.data.converters

import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TracksDbConvertor {
    fun map(track: Track, isLiked: Boolean): TrackEntity {
        return with(track) {
            TrackEntity(
                id = trackId,
                lastLikeUpdate = System.currentTimeMillis(),
                isLiked = isLiked,
                trackName = trackName,
                trackTime = trackTime,
                artistName = artistName,
                artworkUrl100 = artworkUrl100,
                artworkUrl512 = artworkUrl512,
                collectionName = collectionName,
                relizeYear = relizeYear,
                primaryGenreName = primaryGenreName,
                country = country,
                previewUrl = previewUrl
            )
        }
    }

    fun map(trackEntity: TrackEntity): Track {
        return with(trackEntity) {
            Track(
                trackId = id,
                trackName = trackName,
                trackTime = trackTime,
                artistName = artistName,
                artworkUrl100 = artworkUrl100,
                artworkUrl512 = artworkUrl512,
                collectionName = collectionName,
                relizeYear = relizeYear,
                primaryGenreName = primaryGenreName,
                country = country,
                previewUrl = previewUrl
            )
        }
    }

    fun map(tracks: List<TrackEntity>): List<Track> = tracks.map(this::map)

}
