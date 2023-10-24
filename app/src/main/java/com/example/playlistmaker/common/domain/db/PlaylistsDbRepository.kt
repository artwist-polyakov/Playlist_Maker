package com.example.playlistmaker.common.domain.db

import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import kotlinx.coroutines.flow.Flow

interface PlaylistsDbRepository {
    fun allPlaylists(): Flow<List<PlaylistInformation>>
    suspend fun addPlaylist(playlist: PlaylistInformation)
    fun getPlaylistTracks(playlistId: Long): Flow<List<PlaylistInformation>>
}