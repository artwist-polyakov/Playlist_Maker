package com.example.playlistmaker.media.ui.fragments.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
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
import java.lang.Math.max

class PlaylistFragment: Fragment() {
    private var playlistId: String = ""
    private val viewModel: PlaylistViewModel by viewModel { parametersOf(playlistId) }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private var onTrackClickDebounce: (Track) -> Unit = {}

    private lateinit var adapter: TracksAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistId = arguments?.getString(ARG_PLAYLIST_ID) ?: "null"
    }
    override fun onCreateView (
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
//        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.grey_color)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.TappedBackButton)
        }

        binding.returnButton.setOnClickListener {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.TappedBackButton)
        }

        binding.shareButton.setOnClickListener {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.SharePlaylist)
        }

        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.playlistState.observe(viewLifecycleOwner) {
            render(it)
        }
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

                viewModel.handleInteraction(
                    SinglePlaylistScreenInteraction.sendMessage(
                        generateShareMessage(playlist, tracks)
                    )
                )
            }

            is SinglePlaylistScreenState.Basic -> {}

            is SinglePlaylistScreenState.ShowMessageEmptyList -> {
                viewModel.handleInteraction(SinglePlaylistScreenInteraction.toBasicState)
                binding.root.showCustomSnackbar (getString(R.string.playlist_is_empty_warning))
            }
        }
    }

    private fun render(tracks: ArrayList<Track>) {
        if (tracks.isEmpty()) {
            binding.bottomSheet.visibility = View.GONE
            binding.mainLayout.alpha = 1f
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
        binding.tracksQuantity.text =
            requireContext().
            applicationContext.
            resources.getQuantityString(
                R.plurals.tracks,
                playlist.tracksCount,
                playlist.tracksCount
            )
        binding.tracksDuration.text =
            requireContext().
            applicationContext.
            resources.
            getQuantityString(
                R.plurals.playlist_minutes,
                playlist.durationInSeconds.toInt() / 60,
                playlist.durationInSeconds.toInt() / 60
        )
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
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        val desiredHeight = max (
            resources
                .displayMetrics.heightPixels -
                    (resources.displayMetrics.widthPixels +
                            ((24 + 24 + 150) * resources.displayMetrics.density)
                                .toInt()
                            ),
            250
        )
        bottomSheetBehavior.peekHeight = desiredHeight
        binding.mainLayout.alpha = 0.5f
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

    private fun generateMessage(playlist: PlaylistInformation, tracks: ArrayList<Track>): String {
        var message = "${playlist.name}\n"
        message+= "${playlist.description}\n"
        message+= "${requireContext().
        applicationContext.
        resources.getQuantityString(
            R.plurals.tracks,
            playlist.tracksCount,
            playlist.tracksCount
        )} tracks\n"
        for ((index, track) in tracks.withIndex()) {
            message+= "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})\n"
        }
        return message
    }
    private fun generateShareMessage(playlist: PlaylistInformation, tracks: ArrayList<Track>): String {
        var message = "${playlist.name}\n"
        message+= "${playlist.description}\n"
        message+= "${requireContext().
        applicationContext.
        resources.getQuantityString(
            R.plurals.tracks,
            playlist.tracksCount,
            playlist.tracksCount
        )} tracks\n"
        // enumerated loop
        for ((index, track) in tracks.withIndex()) {
            message+= "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})\n"
        }
        return message
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