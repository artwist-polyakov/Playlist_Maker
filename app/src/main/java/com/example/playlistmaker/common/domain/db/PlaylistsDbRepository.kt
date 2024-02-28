package com.example.playlistmaker.common.domain.db

import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsDbRepository {
    fun allPlaylists(): Flow<List<PlaylistInformation>>
    suspend fun addPlaylist(playlist: PlaylistInformation)
    fun getPlaylistTracks(playlistId: String): Flow<List<Track>>
    suspend fun addTrackToPlaylist(playlistId: String, track: Track): Boolean
    suspend fun getPlaylist(playlistId: String): PlaylistInformation
    suspend fun deletePlaylist(playlistId: String)
    suspend fun removeTrackFromPlaylist(playlistId: String, track: Track)
}
