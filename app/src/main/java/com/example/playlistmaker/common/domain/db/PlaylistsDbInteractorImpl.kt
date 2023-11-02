package com.example.playlistmaker.common.domain.db


import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsDbInteractorImpl(
    private val repository: PlaylistsDbRepository
) : PlaylistsDbInteractor {
    override fun giveMeTracksFromPlaylist(playlistId: String): Flow<List<Track>> {
        return repository.getPlaylistTracks(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlistId: String, track: Track): Boolean {
        return repository.addTrackToPlaylist(playlistId, track)
    }

    override fun giveMeAllPlaylists(): Flow<List<PlaylistInformation>> {
        return repository.allPlaylists()
    }

    override suspend fun addPlaylist(playlist: PlaylistInformation) {
        repository.addPlaylist(playlist)
    }

    override suspend fun getPlaylist(playlistId: String): PlaylistInformation {
        return repository.getPlaylist(playlistId)
    }
}