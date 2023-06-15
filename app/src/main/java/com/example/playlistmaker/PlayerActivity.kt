package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.player.ActivityPlayerBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class PlayerActivity: AppCompatActivity() {
    private lateinit var backButton: ImageView
    private lateinit var playButton: FloatingActionButton
    private lateinit var trackCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var trackTimeTitle: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackDurationTitle: TextView
    private lateinit var trackAlbumName: String
    private lateinit var trackAlbumNameTitle: String
    private lateinit var trackReleaseYear: String
    private lateinit var trackReleaseTitle: String
    private lateinit var trackGenre: String
    private lateinit var trackTitle: String
    private lateinit var trackCountry: String
    private lateinit var trackCountryTitle: String
    private lateinit var currentTrack: Track

    companion object {
        const val API_URL = "https://itunes.apple.com"
        const val PREFS = "my_prefs"
        const val TRACK = "current_track"
    }
    override fun onPause() {
        super.onPause()
        saveStateToPrefs(PREFS, TRACK, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun saveStateToPrefs(prefs_name: String, key: String, context: Context) {
        val gson = Gson()
        val json = gson.toJson(currentTrack)
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, json).apply()
    }

    private fun getStateFromPrefs(prefs_name: String, key: String, context: Context) {
        val gson = Gson()
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(key, "[]")
        if ((json != "null") || (json != "[]")) {
            val type = object : TypeToken<Track>() {}.type
            currentTrack = gson.fromJson(json, type)
        } else {
            currentTrack = Track(
                0,
                "",
                "",
                0,
                "",
                "",
                "",
                "",
                ""
            )
        }
    }

    fun clearSharedPreferences(prefs_name: String, key: String, context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(key).apply()
    }

}
