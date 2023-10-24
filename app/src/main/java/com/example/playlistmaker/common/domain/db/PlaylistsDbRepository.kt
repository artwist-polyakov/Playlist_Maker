package com.example.playlistmaker.common.domain.db

import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsDbRepository {
    fun allPlaylists(): Flow<List<PlaylistInformation>>
    suspend fun addPlaylist(playlist: PlaylistInformation)
    fun getPlaylistTracks(playlistId: Long): Flow<List<Track>>
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long)
}