package com.example.playlistmaker.common.data.converters

import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TracksDbConvertor {
    fun map (track: Track, isLiked: Boolean): TrackEntity {
        return TrackEntity(
            id = track.trackId,
            lastLikeUpdate = System.currentTimeMillis(),
            isLiked = isLiked,
            trackName = track.trackName,
            trackTime = track.trackTime,
            artistName = track.artistName,
            artworkUrl100 = track.artworkUrl100,
            artworkUrl512 = track.artworkUrl512,
            collectionName = track.collectionName,
            relizeYear = track.relizeYear,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackId = trackEntity.id,
            trackName = trackEntity.trackName,
            trackTime = trackEntity.trackTime,
            artistName = trackEntity.artistName,
            artworkUrl100 = trackEntity.artworkUrl100,
            artworkUrl512 = trackEntity.artworkUrl512,
            collectionName = trackEntity.collectionName,
            relizeYear = trackEntity.relizeYear,
            primaryGenreName = trackEntity.primaryGenreName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl
        )
    }
}