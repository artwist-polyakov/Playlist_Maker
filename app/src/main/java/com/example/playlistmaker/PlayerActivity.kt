package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Barrier
import androidx.constraintlayout.widget.Group
import androidx.constraintlayout.widget.Guideline
import com.bumptech.glide.Glide
import com.example.playlistmaker.model.Track
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale



class PlayerActivity: AppCompatActivity() {
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
    private lateinit var currentTrack: Track
    private lateinit var barrier: Barrier
    private lateinit var rightGuidline: Guideline

    companion object {
        const val API_URL = "https://itunes.apple.com"
        const val TRACK = "current_track"
    }

    override fun onResume() {
        super.onResume()
        currentTrack = intent.extras?.getParcelable(TRACK)!!
        bindTrackInfo(currentTrack)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentTrack = intent.extras?.getParcelable(TRACK)!!


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

    fun bindTrackInfo (track:Track) {

        // Страна не пустая?
        if (track.country == null) {
            trackCountryInfoGroup.visibility = Group.GONE
        } else {
            trackCountryInfoGroup.visibility = Group.VISIBLE
            trackCountry.text = track.country
        }

        // Трек не пустой?
        if (track.trackName == "") {
            trackInfoGroup.visibility = Group.GONE
        } else {
            trackInfoGroup.visibility = Group.VISIBLE
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackDuration.text =  track.minssecs
            trackAlbumName.text = track.collectionName
            trackReleaseYear.text = track.relizeYear
            trackGenre.text = track.primaryGenreName
            Glide.with(this)
                .load(track.artworkUrl512)
                .into(trackCover)

        }

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
