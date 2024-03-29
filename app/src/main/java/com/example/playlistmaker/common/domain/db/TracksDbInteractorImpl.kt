package com.example.playlistmaker.common.domain.db

import com.example.playlistmaker.common.data.converters.TracksDbConvertor
import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TracksDbInteractorImpl(
    private val tracksDbRepository: TracksDbRepository,
    private val tracksDbConvertor: TracksDbConvertor
) : TracksDbInteractor {
    override fun allLikedTracks(): Flow<List<Track>> {
        return tracksDbRepository.allLikedTracks()
    }

    override suspend fun isTrackLiked(trackId: Long): Boolean {
        return tracksDbRepository.isTrackLiked(trackId)
    }

    override suspend fun switchTrackLikeStatus(track: Track): Boolean {
        return tracksDbRepository.switchTrackLikeStatus(prepereTrackToLike(track))
    }

    private suspend fun prepereTrackToLike(track: Track): TrackEntity {
        val likeStatus = isTrackLiked(track.trackId)
        return tracksDbConvertor.map(track, likeStatus)
    }
}