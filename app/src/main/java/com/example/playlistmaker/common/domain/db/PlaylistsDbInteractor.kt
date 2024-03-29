package com.example.playlistmaker.common.domain.db

import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsDbInteractor {
    fun giveMeTracksFromPlaylist(playlistId: String): Flow<List<Track>>
    suspend fun addTrackToPlaylist(playlistId: String, track: Track): Boolean
    fun giveMeAllPlaylists(): Flow<List<PlaylistInformation>>
    suspend fun addPlaylist(playlist: PlaylistInformation)
    suspend fun getPlaylist(playlistId: String): PlaylistInformation
    suspend fun deletePlaylist(playlist: PlaylistInformation)
    suspend fun removeTrackFromPlaylist(playlistId: String, track: Track)
}