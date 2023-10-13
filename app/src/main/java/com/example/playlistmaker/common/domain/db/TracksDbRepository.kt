package com.example.playlistmaker.common.domain.db

import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksDbRepository {
    fun allLikedTracks(): Flow<List<Track>>
    suspend fun isTrackLiked(trackId: Long): Boolean?
    suspend fun switchTrackLikeStatus(track: TrackEntity)
}