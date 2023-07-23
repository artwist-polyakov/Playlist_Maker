package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.models.TrackInformation
import com.example.playlistmaker.presentation.player.PlayerActivityInterface
import com.example.playlistmaker.presentation.player.PlayerPresenterInterface
import com.example.playlistmaker.presentation.player.PresenterCreator
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
    override var trackCountryInfoGroup: Group? = null
    override var currentTrack: TrackInformation? = null

    companion object {
        const val TRACK = "current_track"
    }

    override fun onResume() {
        super.onResume()
        currentTrack = intent.extras?.getParcelable(TRACK)!!
        currentTrack?.let{
            playerPresenter = PresenterCreator.giveMeMyPresenter(this, it)
            playerPresenter?.bindScreen()
        }
    }

    override fun onPause() {
        super.onPause()
        playerPresenter?.resetPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerPresenter?.resetPlayer()
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
        currentTrack.let {
            playerPresenter = PresenterCreator.giveMeMyPresenter(this, it!!)
            playerPresenter?.changeTrack(it)
            playerPresenter?.bindScreen()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(TRACK, currentTrack)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentTrack = savedInstanceState.getParcelable(TRACK)!!

    }
}
