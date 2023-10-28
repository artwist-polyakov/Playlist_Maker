package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.common.utils.debounce
import com.example.playlistmaker.databinding.ActivitySongPageBinding
import com.example.playlistmaker.player.presentation.PlayerActivityInterface
import com.example.playlistmaker.player.ui.view_model.PlayerBottomSheetState
import com.example.playlistmaker.player.ui.view_model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity(), PlayerActivityInterface {
    private lateinit var binding: ActivitySongPageBinding
    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var currentTrack: TrackInformation
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private var onPlaylistClickDebounce: (PlaylistInformation) -> Unit = {}

    private val adapter: PlayerBottomSheetAdapter = PlayerBottomSheetAdapter(object : PlayerBottomSheetAdapter.PlaylistClickListener {
        override fun onTrackClick(playlist: PlaylistInformation) {
            onPlaylistClickDebounce(playlist)
        }
    })
    private lateinit var recyclerView: RecyclerView

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

        binding.addToCollection.setOnClickListener {
            viewModel.addCollection()
        }


        //BOTTOM SHEET
        val bottomSheetContainer = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // newState — новое состояние BottomSheet
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // загружаем рекламный баннер
                        Log.d("PlayerActivity", "STATE_EXPANDED")
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // останавливаем трейлер
                        viewModel.hideCollection()
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        // возобновляем трейлер
                        viewModel.hideCollection()
                    }
                    else -> {
                        // Остальные состояния не обрабатываем
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        onPlaylistClickDebounce = debounce<PlaylistInformation>(
            CLICK_DEBOUNCE_DELAY,
            lifecycleScope,
            false
        ) { playlist ->
            viewModel.handlePlaylistTap(playlist)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        //BINDING
        viewModel.timerState.observe(this, Observer {
            binding.time.text = it.toString()
        })

        viewModel.bottomSheetState.observe(this, Observer {
            renderBottomSheetState(it)
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

    private fun renderBottomSheetState(state: PlayerBottomSheetState) {
        when(state) {
            is PlayerBottomSheetState.Hidden -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.mainLayout.alpha = 1f
            }
            is PlayerBottomSheetState.Shown -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                binding.mainLayout.alpha = 0.5f
                Log.d("PlayerActivity", "renderBottomSheetState: ${state.playlists}")
                adapter.updatePlaylists(state.playlists)
            }
            is PlayerBottomSheetState.PlaylistAdded -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.mainLayout.alpha = 1f
                showSuccess(state)
            }
            else -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.mainLayout.alpha = 0.5f
            }
        }
    }

    private fun showSuccess(state: PlayerBottomSheetState) {
        var text: String = ""
        when (state) {
            is PlayerBottomSheetState.PlaylistAdded -> {
                text = getString(R.string.track_added, state.playlist.name)
            }
            is PlayerBottomSheetState.PlaylistNotAdded  -> {
                text = getString(R.string.track_already_in, state.playlist.name)
            }
            else -> {
                text = ""
            }
        }
        val typedValue = TypedValue()
        theme.resolveAttribute(
            androidx.transition.R.attr.colorPrimary,
            typedValue,
            true
        )
        val textColor = typedValue.data

        theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnPrimary,
            typedValue,
            true
        )
        val backgroundColor = typedValue.data

        val snackbar = Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_SHORT
        )
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(backgroundColor)
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(textColor)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackbar.show()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 10L
    }
}

