package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.databinding.ActivitySongPageBinding
import com.example.playlistmaker.player.presentation.PlayerActivityInterface
import com.example.playlistmaker.player.ui.view_model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlayerActivity : AppCompatActivity(), PlayerActivityInterface {
    private lateinit var binding: ActivitySongPageBinding
    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var currentTrack: TrackInformation

    companion object {
        const val TRACK = "track"
        const val START_TIME = "00:00"
    }

    override fun showTrackInfo(trackInfo: TrackInformation) {
        showPreparationState()
        if (trackInfo.country == null) {
            binding.trackCountryInfo.visibility = Group.GONE
        } else {
            binding.trackCountryInfo.visibility = Group.VISIBLE
            binding.countryValue.text = trackInfo.country
        }
        // Трек не пустой?
        if (trackInfo.trackName == "") {
            binding.trackInfo.visibility = Group.GONE
        } else {
            binding.trackInfo.visibility = Group.VISIBLE
            binding.songTitle.text = trackInfo.trackName
            binding.artistName.text = trackInfo.artistName
            binding.durationTime.text = trackInfo.trackTime
            binding.albumName.text = trackInfo.collectionName
            binding.yearValue.text = trackInfo.relizeYear
            binding.genreValue.text = trackInfo.primaryGenreName
            Glide.with(this)
                .load(trackInfo.artworkUrl512)
                .into(this.binding.trackCover)
        }
        setTime(START_TIME)
    }

    override fun showPlayState() {
        binding.playButton.setImageResource(R.drawable.pause_button)
    }

    override fun showPauseState() {
        binding.playButton.setImageResource(R.drawable.play_button)
    }

    override fun showPreparationState() {
        binding.playButton.isEnabled = false
    }

    override fun showReadyState() {
        binding.playButton.isEnabled = true
    }

    override fun setTime(time: String) {
        binding.time.text = time
    }

    override fun onResume() {
        super.onResume()
        currentTrack = intent.extras?.getParcelable(TRACK)!!
        currentTrack?.let {
            showTrackInfo(it)
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTrack = intent.extras?.getParcelable(TRACK)!!

        // BACK BUTTON
        binding.returnButton.setOnClickListener {
            viewModel.resetPlayer()
            finish()
        }
        // PLAYER INTERFACE
        binding.playButton.setOnClickListener {
            viewModel.playPause()
        }

        //BINDING
        viewModel.giveCurrentTrack()?.let { showTrackInfo(it) }
//        viewModel.changeTrack(currentTrack)
        viewModel.playerState.observe(this, Observer { state ->
            Log.d("currentButtonState", "ObserverSetted")
            when(state) {
                PlayerState.Loading -> {
                    binding.playButton.isEnabled = false
                }
                PlayerState.Ready -> {
                    Log.d("currentButtonState", "Ready")
                    binding.playButton.isEnabled = true
                }
                PlayerState.Play -> {
                    binding.playButton.setImageResource(R.drawable.pause_button)
                }
                PlayerState.Pause -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                }

                is PlayerState.TimeUpdate -> {
                    binding.time.text = state.time.toString()
                }

            }
        })
        viewModel.initializePlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(TRACK, currentTrack)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentTrack = savedInstanceState.getParcelable(TRACK)!!

    }

    override fun onBackPressed() {
        viewModel.resetPlayer() // Остановка и уничтожение плеера
        super.onBackPressed()  // Закрыть текущую активность
    }
}

