package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.data.converters.TracksDbConvertor
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.data.db.entity.TrackEntity
import com.example.playlistmaker.common.domain.db.TracksDbRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TracksDbRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val tracksDbConvertor: TracksDbConvertor,
): TracksDbRepository {
    override fun allLikedTracks(): Flow<List<Track>> =
        appDatabase
            .trackDao()
            .getLikedTracks()
            .distinctUntilChanged()
            .map( tracksDbConvertor::map )

    override suspend fun isTrackLiked(trackId: Long): Boolean =
        appDatabase.trackDao().isTrackLiked(trackId).first() ?: false

    override suspend fun switchTrackLikeStatus(track: TrackEntity): Boolean  =
         appDatabase.trackDao().switchLike(track)
}