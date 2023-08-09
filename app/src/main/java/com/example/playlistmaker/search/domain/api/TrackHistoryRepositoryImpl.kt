package com.example.playlistmaker.search.domain.api

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.LinkedRepository
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.models.Track
import com.example.playlistmaker.search.data.storage.TracksStorage

// TODO: Ввести этот класс вместо LinkedRepository для более человекопонятного названия класса и кода
class TrackHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) : LinkedRepository<TrackDto>(maxSize = 10, sharedPreferences = sharedPreferences),
    TracksStorage {
    override fun pushTrackToHistory(item: TrackDto) {
        TODO("Not yet implemented")
    }

    override fun takeHistory(reverse: Boolean): ArrayList<TrackDto> {
        TODO("Not yet implemented")
    }

    override fun restoreHistory() {
        TODO("Not yet implemented")
    }

    override fun saveHistory() {
        TODO("Not yet implemented")
    }

    override fun clearHistory() {
        TODO("Not yet implemented")
    }

}