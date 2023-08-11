package com.example.playlistmaker.search.data.storage

import com.example.playlistmaker.search.data.dto.TrackDto

interface TracksStorage {
    fun pushTrackToHistory(item: TrackDto)
    fun takeHistory(reverse: Boolean): ArrayList<TrackDto>
    fun restoreHistory()
    fun saveHistory()
    fun clearHistory()
}