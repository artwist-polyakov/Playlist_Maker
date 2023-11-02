package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.common.presentation.showCustomSnackbar
import com.example.playlistmaker.common.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.presentation.PlayerInterface
import com.example.playlistmaker.player.ui.view_model.PlayerBottomSheetState
import com.example.playlistmaker.player.ui.view_model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PlayerFragment : Fragment(), PlayerInterface {
    private val viewModel: PlayerViewModel by viewModel()
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentTrack: TrackInformation
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private var onPlaylistClickDebounce: (PlaylistInformation) -> Unit = {}
    private lateinit var adapter: PlayerBottomSheetAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PlayerBottomSheetAdapter(object : PlayerBottomSheetAdapter.PlaylistClickListener {
            override fun onTrackClick(playlist: PlaylistInformation) {
                onPlaylistClickDebounce(playlist)
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.resetPlayer()
            findNavController().popBackStack()
        }

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        viewModel.giveCurrentTrack()?.let {
            currentTrack = it
        }
        binding.returnButton.setOnClickListener {
            viewModel.resetPlayer()
            findNavController().popBackStack()
        }
        // PLAYER INTERFACE
        setupPlayerInterface()

        //BOTTOM SHEET
        setupBottomSheet()

        //BINDING
        setupObservers()

        renderState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            renderBottomSheetState(it.third ?: PlayerBottomSheetState.Hidden)
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
                findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment)
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
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.timerState.observe(viewLifecycleOwner) {
            binding.time.text = it.toString()
        }

        viewModel.bottomSheetState.observe(viewLifecycleOwner) {
            renderBottomSheetState(it)
        }

        viewModel.giveCurrentTrack()?.let { showTrackInfo(it) }
        viewModel.playerState.observe(viewLifecycleOwner) { state ->
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
        }

        viewModel.likeState.observe(viewLifecycleOwner) {
            renderLikeState(it)
        }
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

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 10L
    }

}