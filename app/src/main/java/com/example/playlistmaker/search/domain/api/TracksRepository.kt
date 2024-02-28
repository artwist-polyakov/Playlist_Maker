package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resource<List<TrackDto>>>
    fun addTrackToHistory(track: TrackDto)
}