package com.example.playlistmaker.common.domain.db

import com.example.playlistmaker.common.data.converters.TracksDbConvertor
import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TracksDbInteractorImpl (
    private val tracksDbRepository: TracksDbRepository,
    private val tracksDbConvertor: TracksDbConvertor
): TracksDbInteractor {
    override fun allLikedTracks(): Flow<List<Track>> {
        return tracksDbRepository.allLikedTracks()
    }

    override fun isTrackLiked(trackId: Long): Flow<Boolean?> {
        return tracksDbRepository.isTrackLiked(trackId)
    }

    override suspend fun switchTrackLikeStatus(track: Track): Flow<Boolean> {
        return tracksDbRepository.switchTrackLikeStatus(prepereTrackToLike(track))
    }

    private suspend fun prepereTrackToLike(track: Track): TrackEntity {
        val likeStatus = isTrackLiked(track.trackId).first() ?: false
        return tracksDbConvertor.map(track, likeStatus)
    }


}