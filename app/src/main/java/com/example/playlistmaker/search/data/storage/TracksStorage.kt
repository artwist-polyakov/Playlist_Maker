package com.example.playlistmaker.search.data.storage

import com.example.playlistmaker.search.models.Track

interface TracksStorage {
    fun pushTrackToHistory(item: Track)
    fun takeHistory(reverse: Boolean): ArrayList<Track>

    fun restoreHistory()

    fun saveHistory()

    fun clearHistory()

}