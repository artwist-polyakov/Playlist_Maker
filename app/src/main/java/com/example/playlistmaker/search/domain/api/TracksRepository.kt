package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.api.Resource
import com.example.playlistmaker.search.data.dto.TrackDto

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<TrackDto>>
    fun addTrackToHistory(track: TrackDto)
}