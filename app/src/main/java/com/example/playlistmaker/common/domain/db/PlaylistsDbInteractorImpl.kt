package com.example.playlistmaker.common.domain.db


import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.media.domain.ImagesRepositoryInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsDbInteractorImpl(
    private val repository: PlaylistsDbRepository,
    private val imagesRepositoryInteractor: ImagesRepositoryInteractor
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

    override suspend fun deletePlaylist(playlist: PlaylistInformation) {
        playlist.image?.let { imagesRepositoryInteractor.removeImage(it) }
        repository.deletePlaylist(playlist.id.toString())
    }

    override suspend fun removeTrackFromPlaylist(playlistId: String, track: Track) {
        repository.removeTrackFromPlaylist(playlistId, track)
    }
}
