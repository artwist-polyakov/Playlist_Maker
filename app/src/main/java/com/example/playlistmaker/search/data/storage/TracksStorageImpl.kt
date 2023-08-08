package com.example.playlistmaker.search.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.LinkedRepository
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.models.Track

class TracksStorageImpl (
    private val sharedPreferences: SharedPreferences
): LinkedRepository<TrackDto>(maxSize = 10, sharedPreferences = sharedPreferences ), TracksStorage {
    override fun pushTrackToHistory(item: TrackDto) {
        super.add(item)
    }

    override fun takeHistory(reverse: Boolean): ArrayList<TrackDto> {
        return super.get(reverse)
    }

    override fun restoreHistory() {
        super.restoreFromSharedPreferences()
    }

    override fun saveHistory() {
        super.saveToSharedPreferences()
    }

    override fun clearHistory() {
        super.clearSharedPreferences()
    }
}