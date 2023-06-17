package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.example.playlistmaker.model.Track
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class PlayerActivity(private var currentTrack: Track): AppCompatActivity() {
    private lateinit var backButton: ImageView
    private lateinit var playButton: FloatingActionButton
    private lateinit var addToCollectionButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var trackCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackAlbumName: TextView
    private lateinit var trackReleaseYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var trackInfoGroup: Group
    private lateinit var trackCountryInfoGroup: Group

    companion object {
        const val API_URL = "https://itunes.apple.com"
        const val PREFS = "my_prefs"
        const val TRACK = "current_track"
    }
    override fun onPause() {
        super.onPause()
        saveStateToPrefs(PREFS, TRACK, this)
    }

    override fun onStop() {
        super.onStop()
        saveStateToPrefs(PREFS, TRACK, this)
    }

    override fun onResume() {
        super.onResume()
        currentTrack = getStateFromPrefs(PREFS, TRACK, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_page)

        // BACK BUTTON
        backButton = findViewById(R.id.return_button)
        backButton.setOnClickListener {
            this.finish()
        }
        // PLAYER INTERFACE
        playButton = findViewById(R.id.play_button)
        addToCollectionButton = findViewById(R.id.add_to_collection)
        likeButton = findViewById(R.id.like_button)
        trackTime = findViewById(R.id.time)

        // TRACK INFO
        trackCover = findViewById(R.id.track_cover)
        trackName = findViewById(R.id.song_title)
        artistName = findViewById(R.id.artist_name)
        trackDuration = findViewById(R.id.duration_time)
        trackAlbumName = findViewById(R.id.album_name)
        trackReleaseYear = findViewById(R.id.year_value)
        trackGenre = findViewById(R.id.genre_value)
        trackCountry = findViewById(R.id.country_value)
        trackInfoGroup = findViewById(R.id.track_info)
        trackCountryInfoGroup = findViewById(R.id.track_country_info)

        //BINDING
        bindTrackInfo(currentTrack)

    }

    private fun saveStateToPrefs(prefs_name: String, key: String, context: Context) {
        val gson = Gson()
        val json = gson.toJson(currentTrack)
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, json).apply()
    }

    private fun getStateFromPrefs(prefs_name: String, key: String, context: Context): Track {
        val gson = Gson()
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(key, "[]")
        if (json != "null") {
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
        return currentTrack
    }

    fun clearSharedPreferences(prefs_name: String, key: String, context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(key).apply()
    }

    fun bindTrackInfo (track:Track) {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(TRACK, currentTrack)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentTrack = savedInstanceState.getParcelable(TRACK)!!
        bindTrackInfo(currentTrack)
    }

}
