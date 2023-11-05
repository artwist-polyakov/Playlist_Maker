package com.example.playlistmaker.media.ui.fragments.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.common.utils.ConfirmationPresenter
import com.example.playlistmaker.common.utils.calculateDesiredHeight
import com.example.playlistmaker.common.utils.debounce
import com.example.playlistmaker.common.utils.setImageUriOrDefault
import com.example.playlistmaker.common.utils.showCustomSnackbar
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.example.playlistmaker.media.ui.view_model.states.SinglePlaylistScreenInteraction
import com.example.playlistmaker.media.ui.view_model.states.SinglePlaylistScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.fragments.TracksAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.android.ext.android.inject

class PlaylistFragment : Fragment() {
    private var playlistId: String = ""
    private val viewModel: PlaylistViewModel by viewModel { parametersOf(playlistId) }
    private val confirmator: ConfirmationPresenter by inject { parametersOf(requireContext()) }
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private var onTrackClickDebounce: (Track) -> Unit = {}

    private lateinit var adapter: TracksAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var optionsBottomSheetBehaviour: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistId = arguments?.getString(ARG_PLAYLIST_ID) ?: "null"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading()
        adapter = TracksAdapter(
            object : TracksAdapter.TrackClickListener {
                override fun onTrackClick(track: Track) {
                    onTrackClickDebounce(track)
                }

                override fun onTrackLongClick(track: Track) {
                    viewModel.handleInteraction(SinglePlaylistScreenInteraction.longTrackTap(track))
                }
            }
        )
        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.TrackClicked(track))
            findNavController().navigate(R.id.action_playlistFragment_to_playerFragment)
        }

        setupBottomSheet()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.TappedBackButton)
        }

        binding.returnButton.setOnClickListener {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.TappedBackButton)
        }

        binding.shareButton.setOnClickListener {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.SharePlaylist)
        }

        binding.dots.setOnClickListener {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.OptionsClicked)
        }

        binding.shareBottomsheetButton.setOnClickListener {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.SharePlaylist)
        }

        binding.editBottomsheetButton.setOnClickListener {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.editPlaylist)
        }

        binding.removeBottomsheetButton.setOnClickListener {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.DeletePlaylist)
        }

        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.playlistState.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: SinglePlaylistScreenState) {
        when (state) {
            is SinglePlaylistScreenState.Loading -> {
                showLoading()
            }

            is SinglePlaylistScreenState.Error -> {
                Log.d("SinglePlaylistScreenState", "Error")
            }

            is SinglePlaylistScreenState.Success -> {
                showLoading()
                configureContent(state.playlist)
                showContent()
            }

            is SinglePlaylistScreenState.GoBack -> {
                findNavController().popBackStack()
            }

            is SinglePlaylistScreenState.SharePlaylistInitiated -> {
                val playlist = state.playlist ?: return
                val tracks = state.tracks ?: return
                viewModel.handleInteraction(SinglePlaylistScreenInteraction.toBasicState)
                viewModel.handleInteraction(
                    SinglePlaylistScreenInteraction.sendMessage(
                        generateShareMessage(playlist, tracks)
                    )
                )
            }

            is SinglePlaylistScreenState.Basic -> {
                optionsBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
            }

            is SinglePlaylistScreenState.ShowMessageEmptyList -> {
                viewModel.handleInteraction(SinglePlaylistScreenInteraction.toBasicState)
                binding.root.showCustomSnackbar(getString(R.string.playlist_is_empty_warning))
            }

            is SinglePlaylistScreenState.ShownOptions -> {
                optionsBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            is SinglePlaylistScreenState.ConfirmDelete -> {
                state.playlist?.let {
                    showPlaylistDeleteConfirmation(it)
                }
            }

            SinglePlaylistScreenState.CancelDelete -> {
                viewModel.handleInteraction(SinglePlaylistScreenInteraction.toBasicState)
                optionsBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
            }

            SinglePlaylistScreenState.DeleteSuccess -> {
                viewModel.handleInteraction(SinglePlaylistScreenInteraction.toBasicState)
                optionsBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                findNavController().popBackStack()
            }

            is SinglePlaylistScreenState.ConfirmTrackDelete -> {
                confirmator.showConfirmationDialog(
                    getString(R.string.remove_track_confirmation_title),
                    getString(R.string.remove_track_confirmation_text),
                    getString(R.string.yes_string),
                    getString(R.string.no_string),
                    {
                        viewModel.handleInteraction(SinglePlaylistScreenInteraction.toBasicState)
                        optionsBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                        viewModel.handleInteraction(
                            SinglePlaylistScreenInteraction.confirmDeleteTrack(state.track)
                        )
                    },
                    {
                        viewModel.handleInteraction(SinglePlaylistScreenInteraction.toBasicState)
                        optionsBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                    },
                    ContextCompat.getColor(requireContext(), R.color.main_screen_background_color),
                    ContextCompat.getColor(requireContext(), R.color.main_screen_background_color)
                )
            }

            SinglePlaylistScreenState.SuccessTrackDelete -> {
                optionsBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                viewModel.refreshData()
            }

            is SinglePlaylistScreenState.EditPlaylist -> {
                val bundle = Bundle().apply {
                    putString("arg_playlist_id", state.playlistId)
                }
                viewModel.handleInteraction(SinglePlaylistScreenInteraction.toBasicState)
                findNavController().navigate(
                    R.id.action_playlistFragment_to_createPlaylistFragment,
                    bundle
                )
            }
        }
    }

    private fun render(tracks: ArrayList<Track>) {
        if (tracks.isEmpty()) {
            binding.bottomSheet.visibility = View.GONE
            binding.root.showCustomSnackbar(getString(R.string.empty_list_notification))
        } else {
            binding.bottomSheet.visibility = View.VISIBLE
            adapter.tracks = tracks
            adapter.notifyDataSetChanged()
        }
    }

    private fun configureContent(playlist: PlaylistInformation) {
        binding.playlistCover.setImageUriOrDefault(
            playlist.image,
            R.drawable.song_cover_placeholder_with_padding
        )
        binding.playlistTitle.text = playlist.name
        binding.playlistDescription.text = playlist.description

        val trackQuantity = generatePluralString(
            playlist.tracksCount,
            R.plurals.tracks
        )

        val trackDuration = generatePluralString(
            playlist.durationInSeconds.toInt() / 60,
            R.plurals.playlist_minutes
        )

        binding.tracksQuantity.text = trackQuantity
        binding.tracksDuration.text = trackDuration
        binding.playlistLittleTitle.text = playlist.name
        binding.playlistLittleCover.setImageUriOrDefault(
            playlist.image,
            R.drawable.song_cover_placeholder
        )
        binding.tracksQuantityLittle.text = trackQuantity
    }

    private fun showContent() {
        binding.playlistCover.visibility = View.VISIBLE
        binding.playlistTitle.visibility = View.VISIBLE
        binding.playlistDescription.visibility = View.VISIBLE
        binding.tracksQuantity.visibility = View.VISIBLE
        binding.tracksDuration.visibility = View.VISIBLE
        binding.shareButton.visibility = View.VISIBLE
        binding.dots.visibility = View.VISIBLE
        binding.loadingIndicator.visibility = View.GONE
    }

    private fun showLoading() {
        binding.playlistCover.visibility = View.GONE
        binding.playlistTitle.visibility = View.GONE
        binding.playlistDescription.visibility = View.GONE
        binding.tracksQuantity.visibility = View.GONE
        binding.tracksDuration.visibility = View.GONE
        binding.shareButton.visibility = View.GONE
        binding.dots.visibility = View.GONE
        binding.loadingIndicator.visibility = View.VISIBLE
    }

    private fun setupBottomSheet() {
        val bottomSheetContainer = binding.bottomSheet
        val optionsBottomSheetContainer = binding.hideableBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        optionsBottomSheetBehaviour = BottomSheetBehavior.from(optionsBottomSheetContainer)

        val desiredHeight = resources.calculateDesiredHeight(24 + 24 + 150)
        val optionsDesiredHeight = resources.calculateDesiredHeight(24 + 28)

        bottomSheetBehavior.peekHeight = desiredHeight
        optionsBottomSheetBehaviour.peekHeight = optionsDesiredHeight
        optionsBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        bottomSheet.post {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }

                    else -> {
                        // Остальные состояния не обрабатываем
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
    }

    private fun generateShareMessage(
        playlist: PlaylistInformation,
        tracks: ArrayList<Track>
    ): String {
        var message = "${playlist.name}\n"
        message += "${playlist.description}\n"
        message += "${
            requireContext().applicationContext.resources.getQuantityString(
                R.plurals.tracks,
                playlist.tracksCount,
                playlist.tracksCount
            )
        }\n"

        for ((index, track) in tracks.withIndex()) {
            message += "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})\n"
        }
        return message
    }

    private fun generatePluralString(quantity: Int, stringId: Int): String {
        return requireContext().applicationContext.resources.getQuantityString(
            stringId,
            quantity,
            quantity
        )
    }

    private fun showPlaylistDeleteConfirmation(playlist: PlaylistInformation) {
        confirmator.showConfirmationDialog(
            getString(R.string.remove_playlist_confirmation_title),
            getString(R.string.remove_playlist_confirmation_text),
            getString(R.string.yes_string),
            getString(R.string.no_string),
            {
                viewModel.handleInteraction(SinglePlaylistScreenInteraction.confirmDelete)
            },
            {
                viewModel.handleInteraction(SinglePlaylistScreenInteraction.toBasicState)
                viewModel.handleInteraction(SinglePlaylistScreenInteraction.cancelDelete)
            },
            ContextCompat.getColor(requireContext(), R.color.main_screen_background_color),
            ContextCompat.getColor(requireContext(), R.color.main_screen_background_color)
        )
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "arg_playlist_id"
        private val CLICK_DEBOUNCE_DELAY = 10L
        fun newInstance(playlistId: String): PlaylistFragment {
            val fragment = PlaylistFragment()
            val args = Bundle()
            args.putString(ARG_PLAYLIST_ID, playlistId)
            fragment.arguments = args
            return fragment
        }
    }
}