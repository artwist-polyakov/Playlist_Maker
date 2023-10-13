package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.databinding.ActivitySongPageBinding
import com.example.playlistmaker.player.presentation.PlayerActivityInterface
import com.example.playlistmaker.player.ui.view_model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity(), PlayerActivityInterface {
    private lateinit var binding: ActivitySongPageBinding
    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var currentTrack: TrackInformation

    override fun showTrackInfo(trackInfo: TrackInformation) {
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
        viewModel.giveCurrentTrack()?.let {
            showTrackInfo(it)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.makeItPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.giveCurrentTrack()?.let{
            currentTrack = it
        }
        // BACK BUTTON
        binding.returnButton.setOnClickListener {
            viewModel.resetPlayer()
            finish()
        }
        // PLAYER INTERFACE
        binding.playButton.setOnClickListener {
            viewModel.playPause()
        }

        binding.likeButton.setOnClickListener {
            viewModel.likeTrack()
        }

        //BINDING
        viewModel.timerState.observe(this, Observer {
            binding.time.text = it.toString()
        })
        viewModel.giveCurrentTrack()?.let { showTrackInfo(it) }
        viewModel.playerState.observe(this, Observer { state ->
            when(state) {
                PlayerState.Loading -> {
                    binding.playButton.isEnabled = false
                }
                PlayerState.Ready -> {
                    binding.playButton.isEnabled = true
                }
                PlayerState.Play -> {
                    binding.playButton.setImageResource(R.drawable.pause_button)
                }
                PlayerState.Pause -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                }
            }
        })

        viewModel.likeState.observe(this, Observer {
            renderLikeState(it)
        })

        renderState()
    }

    override fun onBackPressed() {
        viewModel.resetPlayer()
        super.onBackPressed()
    }

    private fun renderLikeState(isLiked: Boolean) {
        Log.d("restoreLikeState", "launched")
        if (isLiked) {
            binding.likeButton.setImageResource(R.drawable.like_button_active)
        } else {
            binding.likeButton.setImageResource(R.drawable.like_button_inactive)
        }
    }

    private fun renderState(){
        viewModel.restoreState().let{
            binding.time.text = it.second.toString()
            when(it.first) {
                PlayerState.Play -> showPlayState()
                PlayerState.Pause -> showPauseState()
                PlayerState.Loading -> {
                    showPreparationState()
                }
                PlayerState.Ready -> showReadyState()
                else -> {
                    showPreparationState()
                    viewModel.initializePlayer()
                }
            }
        }
    }
}

