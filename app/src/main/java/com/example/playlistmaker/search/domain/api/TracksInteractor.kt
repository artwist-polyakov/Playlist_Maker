package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
    fun addTrackToHistory(track: TrackDto)
}