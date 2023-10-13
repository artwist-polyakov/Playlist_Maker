package com.example.playlistmaker.common.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksDbInteractor {

    fun allLikedTracks(): Flow<List<Track>>

    fun isTrackLiked(trackId: Long): Flow<Boolean?>

    suspend fun switchTrackLikeStatus(track: Track): Flow<Boolean>
}