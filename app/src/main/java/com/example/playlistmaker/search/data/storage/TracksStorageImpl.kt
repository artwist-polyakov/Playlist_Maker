package com.example.playlistmaker.search.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.storage.TracksStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TracksStorageImpl(
    context: Context
) : TracksStorage {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    private val trackSet: LinkedHashSet<TrackDto> = LinkedHashSet()

    private fun addTrack(track: TrackDto) {
        if (trackSet.contains(track)) {
            trackSet.remove(track)
        } else if (trackSet.size >= CAPACITY) {
            val firstTrack = trackSet.iterator().next()
            trackSet.remove(firstTrack)
        }
        trackSet.add(track)
    }

    private fun invertTrackList(): ArrayList<TrackDto> {
        return ArrayList(trackSet.toList().reversed())
    }

    override fun toString(): String {
        return trackSet.toString()
    }

    private fun clear() {
        trackSet.clear()
    }

    override fun pushTrackToHistory(item: TrackDto) {
        addTrack(item)
        saveHistory()
    }

    override fun takeHistory(reverse: Boolean): ArrayList<TrackDto> {
        restoreHistory()
        return if (reverse) {
            invertTrackList()
        } else {
            ArrayList(trackSet)
        }
    }

    override fun restoreHistory() {
        val key = HISTORY_KEY
        val gson = Gson()
        clear()
        val json = sharedPreferences.getString(key, "[]")
        if (json != null && json != "null" && json != "[]") {
            val type = object : TypeToken<List<TrackDto>>() {}.type
            val list: List<TrackDto> = gson.fromJson(json, type)
            trackSet.addAll(list)
        } else {
            clearHistory()
        }
    }

    override fun saveHistory() {
        val key = HISTORY_KEY
        val gson = Gson()
        val json = gson.toJson(trackSet)
        sharedPreferences.edit().putString(key, json).apply()
    }

    override fun clearHistory() {
        val key = HISTORY_KEY
        sharedPreferences.edit().remove(key).apply()
        clear()
    }

    private companion object {
        const val HISTORY_KEY = "HISTORY_KEY"
        const val CAPACITY = 10
    }

}