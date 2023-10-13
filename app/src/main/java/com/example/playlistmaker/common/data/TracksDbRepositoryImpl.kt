package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.data.converters.TracksDbConvertor
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.common.domain.db.TracksDbRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksDbRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val tracksDbConvertor: TracksDbConvertor,
): TracksDbRepository {
    override fun allLikedTracks(): Flow<List<Track>> = flow {
        val likedTracks = appDatabase.trackDao().getLikedTracks()
        emit(likedTracks.map { tracksDbConvertor.map(it) })
    }

    override suspend fun isTrackLiked(trackId: Long): Boolean? {
        return appDatabase.trackDao().isTrackLiked(trackId)
    }

    override suspend fun switchTrackLikeStatus(track: TrackEntity) {
        appDatabase.trackDao().switchLike(track)
    }


}