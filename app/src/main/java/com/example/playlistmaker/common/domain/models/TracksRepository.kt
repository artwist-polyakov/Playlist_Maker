package com.example.playlistmaker.common.domain.models

import android.content.Context

interface TracksRepository {
    fun add(item: Track)
    fun get(reverse: Boolean): ArrayList<Track>?

    fun restoreFromSharedPreferences(prefs_name: String, key: String, context: Context)

    fun saveToSharedPreferences(prefs_name: String, key: String, context: Context)

    fun clearSharedPreferences(prefs_name: String, key: String, context: Context)

}