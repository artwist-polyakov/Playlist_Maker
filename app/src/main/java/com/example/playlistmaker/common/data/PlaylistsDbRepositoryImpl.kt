package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.data.converters.PlaylistsDbConverter
import com.example.playlistmaker.common.data.converters.TracksDbConvertor
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.domain.db.PlaylistsDbRepository
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlaylistsDbRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val playlistsDbConvertor: PlaylistsDbConverter,
    private val tracksDbConvertor: TracksDbConvertor
): PlaylistsDbRepository {
    override fun allPlaylists(): Flow<List<PlaylistInformation>> =
        appDatabase.playlistDao().getPlaylists().distinctUntilChanged().map { playlists ->
            playlists.map(playlistsDbConvertor::map)
        }

    override suspend fun addPlaylist(playlist: PlaylistInformation) {
        appDatabase.playlistDao().insertPlaylist(playlistsDbConvertor.map(playlist))
    }

    override fun getPlaylistTracks(playlistId: Long): Flow<List<Track>> =
        appDatabase.playlistDao().getTracksFromPlaylist(playlistId).distinctUntilChanged()
            .map { playlists ->
                playlists.map(tracksDbConvertor::map)
            }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        appDatabase.playlistDao().insertTrackPlaylistTrackReference(playlistsDbConvertor.map(playlistId, trackId))
    }

}