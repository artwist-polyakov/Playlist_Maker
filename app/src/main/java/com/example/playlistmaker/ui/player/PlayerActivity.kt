package com.example.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Barrier
import androidx.constraintlayout.widget.Group
import androidx.constraintlayout.widget.Guideline
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerActivity : AppCompatActivity() {
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
    private lateinit var handler: Handler
    private var updateTimeRunnable: Runnable = Runnable { }

    companion object {
        const val API_URL = "https://itunes.apple.com"
        const val TRACK = "current_track"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    override fun onResume() {
        super.onResume()
        currentTrack = intent.extras?.getParcelable(TRACK)!!
        bindTrackInfo(currentTrack)
    }

    override fun onPause() {
        super.onPause()
        if (playerState == 2) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (playerState != 0) {
            mediaPlayer.release()
            handler.removeCallbacks(updateTimeRunnable)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_song_page)

        currentTrack = intent.extras?.getParcelable(TRACK)!!


        // BACK BUTTON
        backButton = findViewById(R.id.return_button)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
        handler = Handler(Looper.getMainLooper())

        preparePlayer()
    }

    fun bindTrackInfo(track: Track) {

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
            trackDuration.text = track.minssecs
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

    private fun preparePlayer() {
        if (currentTrack.previewUrl != null) {
            mediaPlayer.apply {
                setDataSource(currentTrack.previewUrl)
                prepareAsync()
                setOnPreparedListener {
                    playButton.isEnabled = true
                    playerState = STATE_PREPARED
                }
            }
            mediaPlayer.setOnCompletionListener {
                playButton.setImageResource(R.drawable.play_button)
                playerState = STATE_PREPARED
                trackTime.text = resources.getString(R.string.time_placeholder)
                handler.removeCallbacks(updateTimeRunnable)
            }
            playButton.setOnClickListener {
                playbackControl()
            }
        } else {
            playButton.isEnabled = false
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        updateTime()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun updateTime() {
        val text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        trackTime.text = text
        updateTimeRunnable = Runnable { updateTime() }
        handler.postDelayed(updateTimeRunnable, 400)
    }


}
