package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.data.dto.TrackDto
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<TrackDto>?, String?>>
    fun addTrackToHistory(track: TrackDto)
}