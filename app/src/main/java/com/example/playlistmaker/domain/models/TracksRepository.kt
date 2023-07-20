package com.example.playlistmaker.domain.models

import android.content.Context
import com.example.playlistmaker.data.dto.TrackDto

interface TracksRepository {
    fun add(item: TrackDto)
    fun get(reverse: Boolean): ArrayList<TrackDto>?

    fun restoreFromSharedPreferences(prefs_name: String, key: String, context: Context)

    fun saveToSharedPreferences(prefs_name: String, key: String, context: Context)

    fun clearSharedPreferences (prefs_name: String, key: String, context: Context)

}