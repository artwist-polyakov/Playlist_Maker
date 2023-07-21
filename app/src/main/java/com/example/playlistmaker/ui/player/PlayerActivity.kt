package com.example.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.example.playlistmaker.R
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.presentation.player.PlayerActivityInterface
import com.example.playlistmaker.presentation.player.PlayerPresenter
import com.example.playlistmaker.presentation.player.PlayerPresenterInterface
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity(), PlayerActivityInterface {
    override var playerPresenter: PlayerPresenterInterface? = null
    private lateinit var backButton: ImageView
    override var playButton: FloatingActionButton? = null
    private lateinit var addToCollectionButton: ImageButton
    private lateinit var likeButton: ImageButton
    override var trackCover: ImageView? = null
    override var trackName: TextView? = null
    override var artistName: TextView? = null
    override var trackTime: TextView? = null
    override var trackDuration: TextView? = null
    override var trackAlbumName: TextView? = null
    override var trackReleaseYear: TextView? = null
    override var trackGenre: TextView? = null
    override var trackCountry: TextView? = null
    override var trackInfoGroup: Group? = null
    var mediaPlayerPresenter: PlayerPresenterInterface? = null
    override var trackCountryInfoGroup: Group? = null


    override var currentTrack: TrackDto? = null


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
        playerPresenter = PlayerPresenter(this)
        playerPresenter!!.bindScreen()

    }

    override fun onPause() {
        super.onPause()
        if (playerState == 2) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerPresenter!!.resetPlayer()
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
        playerPresenter = PlayerPresenter(this)
        playerPresenter!!.bindScreen()
        handler = Handler(Looper.getMainLooper())
        preparePlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(TRACK, currentTrack)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentTrack = savedInstanceState.getParcelable(TRACK)!!
        playerPresenter = PlayerPresenter(this)
        playerPresenter!!.bindScreen()
    }

    private fun preparePlayer() {
        if (currentTrack?.previewUrl != null) {
            mediaPlayer.apply {
                setDataSource(currentTrack!!.previewUrl)
                prepareAsync()
                setOnPreparedListener {
                    playButton!!.isEnabled = true
                    playerState = STATE_PREPARED
                }
            }
            mediaPlayer.setOnCompletionListener {
                playerPresenter!!.resetPlayer()
                playerState = STATE_PREPARED
                trackTime?.text = resources.getString(R.string.time_placeholder)
                handler.removeCallbacks(updateTimeRunnable)
            }
            playButton!!.setOnClickListener {
                playbackControl()
            }
        } else {
            playButton!!.isEnabled = false
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
        playerPresenter!!.changePlayButton()
        playerState = STATE_PLAYING
        updateTime()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerPresenter!!.changePlayButton()
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun updateTime() {
        val text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        trackTime?.text = text
        updateTimeRunnable = Runnable { updateTime() }
        handler.postDelayed(updateTimeRunnable, 400)
    }


}
