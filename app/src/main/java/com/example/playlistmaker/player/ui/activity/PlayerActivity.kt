package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
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
import com.example.playlistmaker.common.presentation.showCustomSnackbar
import com.example.playlistmaker.common.utils.debounce
import com.example.playlistmaker.databinding.ActivitySongPageBinding
import com.example.playlistmaker.media.ui.fragments.create.CreatePlaylistFragment
import com.example.playlistmaker.media.ui.fragments.create.CreatePlaylistInterface
import com.example.playlistmaker.player.presentation.PlayerInterface
import com.example.playlistmaker.player.ui.view_model.PlayerBottomSheetState
import com.example.playlistmaker.player.ui.view_model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity(), PlayerInterface {
    private lateinit var binding: ActivitySongPageBinding
    private val viewModel: PlayerViewModel by viewModel()
    private lateinit var currentTrack: TrackInformation
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private var onPlaylistClickDebounce: (PlaylistInformation) -> Unit = {}

    private val adapter: PlayerBottomSheetAdapter =
        PlayerBottomSheetAdapter(object : PlayerBottomSheetAdapter.PlaylistClickListener {
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
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        binding = ActivitySongPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.giveCurrentTrack()?.let {
            currentTrack = it
        }
        // BACK BUTTON
        binding.returnButton.setOnClickListener {
            viewModel.resetPlayer()
            finish()
        }
        // PLAYER INTERFACE
        setupPlayerInterface()


        //BOTTOM SHEET
        setupBottomSheet()

        //BINDING
        setupObservers()

        binding.navGraphPlayer.visibility = View.GONE
        renderState()
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_graph_player)
        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull()

        if (binding.navGraphPlayer.visibility == View.GONE) {
            viewModel.resetPlayer()
            super.onBackPressed()
            finish()
        } else if (currentFragment != null && currentFragment is CreatePlaylistInterface) {
            currentFragment.emulateBackButton()
        } else {
            super.onBackPressed()
            viewModel.clearPlaylistFragment()
        }
    }

    private fun renderLikeState(isLiked: Boolean) {
        if (isLiked) {
            binding.likeButton.setImageResource(R.drawable.like_button_active)
        } else {
            binding.likeButton.setImageResource(R.drawable.like_button_inactive)
        }
    }

    private fun renderState() {
        viewModel.restoreState().let {
            binding.time.text = it.second.toString()
            when (it.first) {
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
            renderBottomSheetState(it.third?: PlayerBottomSheetState.Hidden)
        }
        viewModel.restorePlaylistFragment()?.let {

            it.returningClosure = {
                binding.navGraphPlayer.visibility = View.GONE
                binding.mainLayout.alpha = 1f
                viewModel.clearPlaylistFragment()
            }

            binding.navGraphPlayer.visibility = View.VISIBLE
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_graph_player, it)
                .commit()
        }
    }

    private fun renderBottomSheetState(state: PlayerBottomSheetState) {
        when (state) {
            is PlayerBottomSheetState.Hidden -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.mainLayout.alpha = 1f
            }

            is PlayerBottomSheetState.Shown -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                binding.mainLayout.alpha = 0.5f
                adapter.updatePlaylists(state.playlists)
            }

            is PlayerBottomSheetState.PlaylistAdded -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.mainLayout.alpha = 1f
                showSuccess(state)
            }

            is PlayerBottomSheetState.PlaylistNotAdded -> {
                showSuccess(state)
            }

            is PlayerBottomSheetState.NewPlaylist -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.mainLayout.alpha = 1f
                binding.navGraphPlayer.visibility = View.VISIBLE

//                val navController = findNavController(R.id.nav_graph_player)
//                navController.navigate(R.id.createPlaylistFragment2)
                val createFragment = CreatePlaylistFragment()
                createFragment.returningClosure = {
                    binding.navGraphPlayer.visibility = View.GONE
                    binding.mainLayout.alpha = 1f
                    viewModel.clearPlaylistFragment()
//                    viewModel.initializePlayer()
                }
                viewModel.savePlaylistFragment(createFragment)
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_graph_player, createFragment)
                    .commit()

            }
        }
    }

    private fun showSuccess(state: PlayerBottomSheetState) {
        val text = when (state) {
            is PlayerBottomSheetState.PlaylistAdded -> {
                getString(R.string.track_added, state.playlist.name)
            }

            is PlayerBottomSheetState.PlaylistNotAdded -> {
                getString(R.string.track_already_in, state.playlist.name)
            }

            else -> ""

        }

        binding.root.showCustomSnackbar(text)
    }

    private fun setupBottomSheet() {
        val bottomSheetContainer = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // newState — новое состояние BottomSheet
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d("PlayerActivity", "STATE_EXPANDED")
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        viewModel.hideCollection()
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
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
        recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.timerState.observe(this, Observer {
            binding.time.text = it.toString()
        })

        viewModel.bottomSheetState.observe(this, Observer {
            renderBottomSheetState(it)
        })

        viewModel.giveCurrentTrack()?.let { showTrackInfo(it) }
        viewModel.playerState.observe(this, Observer { state ->
            when (state) {
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
    }

    private fun setupPlayerInterface() {
        binding.playButton.setOnClickListener {
            viewModel.playPause()
        }

        binding.likeButton.setOnClickListener {
            viewModel.likeTrack()
        }

        binding.addToCollection.setOnClickListener {
            viewModel.addCollection()
        }

        binding.createButton.setOnClickListener {
            viewModel.handleNewPlaylistTap()
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 10L
    }
}

