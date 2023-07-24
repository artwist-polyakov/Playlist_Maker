package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.models.TrackInformation
import com.example.playlistmaker.presentation.player.PlayerActivityInterface
import com.example.playlistmaker.presentation.player.PlayerPresenter
import com.example.playlistmaker.presentation.player.PlayerPresenterInterface
import com.example.playlistmaker.presentation.player.PresenterCreator
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlayerActivity : AppCompatActivity(), PlayerActivityInterface {
    var playerPresenter: PlayerPresenterInterface? = null
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
    private lateinit var currentTrack: TrackInformation

    companion object {
        const val TRACK = "current_track"
        const val START_TIME = "00:00"
    }

    override fun showTrackInfo(trackInfo: TrackInformation) {
        showPreparationState()
        if (trackInfo.country == null) {
            trackCountryInfoGroup.visibility = Group.GONE
        } else {
            trackCountryInfoGroup.visibility = Group.VISIBLE
            trackCountry.text = trackInfo.country
        }
        // Трек не пустой?
        if (trackInfo.trackName == "") {
            trackInfoGroup.visibility = Group.GONE
        } else {
            trackInfoGroup.visibility = Group.VISIBLE
            trackName.text = trackInfo.trackName
            artistName.text = trackInfo.artistName
            trackDuration.text = trackInfo.trackTime
            trackAlbumName.text = trackInfo.collectionName
            trackReleaseYear.text = trackInfo.relizeYear
            trackGenre.text = trackInfo.primaryGenreName
            Glide.with(this)
                .load(trackInfo.artworkUrl512)
                .into(this.trackCover)
        }
        playerPresenter?.setPlayPauseUseCase(playButton)
        setTime(START_TIME)


    }

    override fun showPlayState() {
        playButton.setImageResource(R.drawable.pause_button)
    }

    override fun showPauseState() {
        playButton.setImageResource(R.drawable.play_button)
    }

    override fun showPreparationState() {
        playButton.isEnabled = false
    }

    override fun showReadyState() {
        playButton.isEnabled = true
    }

    override fun setTime(time: String) {
        trackTime.text = time
    }

    override fun onResume() {
        super.onResume()
        currentTrack = intent.extras?.getParcelable(TRACK)!!
        currentTrack?.let {
            playerPresenter = PresenterCreator.giveMeMyPresenter(this, it) { view, track ->
                PlayerPresenter(view, track)
            }
            showTrackInfo(it)
        }
        playerPresenter?.setPlayPauseUseCase(playButton)

    }

    override fun onPause() {
        super.onPause()
//        playerPresenter?.resetPlayer()
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
        showTrackInfo(currentTrack)
        playerPresenter = PresenterCreator.giveMeMyPresenter(this, currentTrack) { view, track ->
            PlayerPresenter(view, track)
        }
        playerPresenter?.setPlayPauseUseCase(playButton)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(TRACK, currentTrack)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentTrack = savedInstanceState.getParcelable(TRACK)!!
        playerPresenter = PresenterCreator.giveMeMyPresenter(this, currentTrack) { view, track ->
            PlayerPresenter(view, track)
        }
    }
}

