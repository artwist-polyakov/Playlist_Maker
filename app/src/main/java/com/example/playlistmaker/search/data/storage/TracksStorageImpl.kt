package com.example.playlistmaker.search.data.storage

import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.search.data.dto.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TracksStorageImpl (
    private val sharedPreferences: SharedPreferences
): TracksStorage {
    private companion object {
        const val HISTORY_KEY = "HISTORY_KEY"
    }
    private val trackSet: LinkedHashSet<TrackDto> = LinkedHashSet()

    private fun addTrack(track: TrackDto) {
        if (trackSet.contains(track)) {
            trackSet.remove(track)
        } else if (trackSet.size >= 10) {
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
        var result: ArrayList<TrackDto>
        if (reverse) {
            result = invertTrackList()
        } else {
            result = ArrayList(trackSet)

            }
        return result
    }

    override fun restoreHistory() {
        val key = HISTORY_KEY
        val gson = Gson()
        clear()
        val json = sharedPreferences.getString(key, "[]")
        Log.d("SharedPreferences", "Восстанавливаем из SharedPreferences: $json")
        if ((json != "null") || (json != "[]")) {
            val type = object : TypeToken<List<TrackDto>>() {}.type
            val list: List<TrackDto> = gson.fromJson(json, type)
            for (item in list) {
                addTrack(item)
            }
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
}