package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.data.converters.TracksDbConvertor
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.common.domain.db.TracksDbRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class TracksDbRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val tracksDbConvertor: TracksDbConvertor,
): TracksDbRepository {
    override fun allLikedTracks(): Flow<List<Track>> = flow {
        appDatabase.trackDao().getLikedTracks().collect { likedTracks ->
            emit(likedTracks.map { tracksDbConvertor.map(it) })
        }
    }

    override fun isTrackLiked(trackId: Long): Flow<Boolean?> = flow {
         emit(appDatabase.trackDao().isTrackLiked(trackId).first())
    }

    override fun switchTrackLikeStatus(track: TrackEntity) = flow {
        emit(appDatabase.trackDao().switchLike(track))
    }


}