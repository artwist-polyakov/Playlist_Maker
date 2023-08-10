package com.example.playlistmaker.search.data.storage

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.models.Track

interface TracksStorage {
    fun pushTrackToHistory(item: TrackDto)
    fun takeHistory(reverse: Boolean): ArrayList<TrackDto>
    fun restoreHistory()
    fun saveHistory()
    fun clearHistory()
}