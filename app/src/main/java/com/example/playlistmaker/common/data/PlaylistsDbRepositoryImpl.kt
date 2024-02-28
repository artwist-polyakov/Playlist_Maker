package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.data.converters.PlaylistsDbConverter
import com.example.playlistmaker.common.data.converters.TracksDbConvertor
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.domain.db.PlaylistsDbRepository
import com.example.playlistmaker.common.domain.db.PrettifyDbRepository
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlaylistsDbRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val prettyfyDbRepository: PrettifyDbRepository,
    private val playlistsDbConvertor: PlaylistsDbConverter,
    private val tracksDbConvertor: TracksDbConvertor
) : PlaylistsDbRepository {

    override fun allPlaylists(): Flow<List<PlaylistInformation>> =
        appDatabase
            .playlistDao()
            .getPlaylists()
            .distinctUntilChanged()
            .map(playlistsDbConvertor::map)

    override suspend fun addPlaylist(playlist: PlaylistInformation) {
        prettyfyDbRepository.stopPrettify()
        appDatabase
            .playlistDao()
            .insertPlaylist(playlistsDbConvertor.map(playlist))
    }

    override fun getPlaylistTracks(playlistId: String): Flow<List<Track>> =
        appDatabase
            .playlistDao()
            .getTracksFromPlaylist(playlistId)
            .distinctUntilChanged()
            .map(tracksDbConvertor::map)

    override suspend fun addTrackToPlaylist(playlistId: String, track: Track): Boolean {
        prettyfyDbRepository.stopPrettify()
        if (!appDatabase
                .trackDao()
                .isTrackExist(track.trackId)
        ) {
            appDatabase
                .trackDao()
                .insertTrack(tracksDbConvertor.map(track, false))
        }
        return appDatabase
            .playlistDao()
            .addTrackToPlaylist(playlistsDbConvertor.map(playlistId, track.trackId))
    }

    override suspend fun getPlaylist(playlistId: String): PlaylistInformation =
        playlistsDbConvertor.map(
            appDatabase
                .playlistDao()
                .givePlaylistWithTime(playlistId)
        )

    override suspend fun deletePlaylist(playlistId: String) {
        appDatabase
            .playlistDao()
            .deletePlaylist(playlistId)
        prettyfyDbRepository.startPrettify()
    }

    override suspend fun removeTrackFromPlaylist(playlistId: String, track: Track) {
        appDatabase
            .playlistDao()
            .removeTrackFromPlaylist(playlistsDbConvertor.map(playlistId, track.trackId))
        prettyfyDbRepository.startPrettify()
    }
}
