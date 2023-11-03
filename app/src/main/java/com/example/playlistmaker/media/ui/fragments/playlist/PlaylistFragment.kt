package com.example.playlistmaker.media.ui.fragments.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.common.utils.setImageUriOrDefault
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.example.playlistmaker.media.ui.view_model.states.SinglePlaylistScreenInteraction
import com.example.playlistmaker.media.ui.view_model.states.SinglePlaylistScreenState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment: Fragment() {
    private var playlistId: String = ""
    private val viewModel: PlaylistViewModel by viewModel { parametersOf(playlistId) }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!


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

//        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.grey_color)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.TappedBackButton)
        }
        binding.returnButton.setOnClickListener {
            viewModel.handleInteraction(SinglePlaylistScreenInteraction.TappedBackButton)
        }
        viewModel.state.observe(viewLifecycleOwner) {
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
            is SinglePlaylistScreenState.Empty -> {
                Log.d("SinglePlaylistScreenState", "Empty")
            }
            is SinglePlaylistScreenState.Success -> {
                showLoading()
                configureContent(state.playlist)
                showContent()
            }

            is SinglePlaylistScreenState.GoBack -> {
                findNavController().popBackStack()
            }
            is SinglePlaylistScreenState.SharePlaylistInitiated -> Log.d("SinglePlaylistScreenState", "SharePlaylistInitiated")
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

    companion object {

        private const val ARG_PLAYLIST_ID = "arg_playlist_id"

        fun newInstance(playlistId: String): PlaylistFragment {
            val fragment = PlaylistFragment()
            val args = Bundle()
            args.putString(ARG_PLAYLIST_ID, playlistId)
            fragment.arguments = args
            return fragment
        }
    }
}