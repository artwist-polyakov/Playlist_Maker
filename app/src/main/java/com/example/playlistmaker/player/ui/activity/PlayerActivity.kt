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

    companion object {
        const val TRACK = "track"
    }

    override fun showTrackInfo(trackInfo: TrackInformation) {
        Log.d("currentButtonState", "SHOWTRACK INFO DISABLED")
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
//        renderState()


        viewModel.giveCurrentTrack()?.let {
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
        //BINDING
        viewModel.timerState.observe(this, Observer {
            binding.time.text = it.toString()
        })
        viewModel.giveCurrentTrack()?.let { showTrackInfo(it) }
//        viewModel.changeTrack(currentTrack)
        Log.d("currentButtonState", "PRE ObserverSetted")
        viewModel.playerState.observe(this, Observer { state ->
            Log.d("currentButtonState", "INSIDE OF OBSERVER")
            when(state) {
                PlayerState.Loading -> {
                    Log.d("currentButtonState", "Loading")
                    binding.playButton.isEnabled = false
                }
                PlayerState.Ready -> {
                    Log.d("currentButtonState", "Ready")
                    Log.d( "currentButtonState", "Ready ${binding.playButton.hashCode()}")
                    binding.playButton.isEnabled = true
                }
                PlayerState.Play -> {
                    Log.d("currentButtonState", "Play")
                    binding.playButton.setImageResource(R.drawable.pause_button)
                }
                PlayerState.Pause -> {
                    Log.d("currentButtonState", "Pause")
                    binding.playButton.setImageResource(R.drawable.play_button)
                }
            }
        })
        val hash = viewModel.hashCode()
        Log.d("currentButtonState", "POST ObserverSetted, $hash")
        renderState()
    }

    override fun onBackPressed() {
        viewModel.resetPlayer() // Остановка и уничтожение плеера
        super.onBackPressed()  // Закрыть текущую активность
    }

    private fun renderState(){
        viewModel.restoreState().let{
            Log.d("currentButtonState", "RESTORE STATE $it")
            binding.time.text = it.second.toString()
            when(it.first) {
                PlayerState.Play -> showPlayState()
                PlayerState.Pause -> showPauseState()
                PlayerState.Loading -> {
                    Log.d("currentButtonState", "LOADING BRANCH RENDER STATE")
                    showPreparationState()
                }
                PlayerState.Ready -> showReadyState()
                else -> {
                    Log.d("currentButtonState", "ELSE BRANCH RENDER STATE")
                    showPreparationState()
                    viewModel.initializePlayer()
                }
            }
        }
    }

}

