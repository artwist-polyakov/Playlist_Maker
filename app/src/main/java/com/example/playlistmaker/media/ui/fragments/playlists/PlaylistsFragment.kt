package com.example.playlistmaker.media.ui.fragments.playlists

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.common.utils.debounce
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.media.ui.view_model.states.PlaylistsScreenInteraction
import com.example.playlistmaker.media.ui.view_model.states.PlaylistsScreenState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private val viewModel: PlaylistsViewModel by viewModel()
    companion object {
        fun newInstance() = PlaylistsFragment()
        private const val CLICK_DEBOUNCE_DELAY = 10L
    }
    private var onPlaylistClickDebounce: (PlaylistInformation) -> Unit = {}

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val adapter: PlaylistsAdapter = PlaylistsAdapter(object : PlaylistsAdapter.PlaylistClickListener {
        override fun onTrackClick(playlist: PlaylistInformation) {
            onPlaylistClickDebounce(playlist)
        }
    })
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clearScreen()
        onPlaylistClickDebounce = debounce<PlaylistInformation>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            viewModel.handleInteraction(PlaylistsScreenInteraction.PlaylistClicked(playlist))
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }

        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.GoToPlaylist -> {
                Log.d("PlaylistsFragment", "GoToPlaylist ${state.playlist}")
            }
            is PlaylistsScreenState.Content -> {
                showPlaylists()
                adapter.updatePlaylists(state.content)
            }

            is PlaylistsScreenState.Empty -> {
                showEmptyState()
            }

            is PlaylistsScreenState.NewPlaylistInitiated -> {
                findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
            }
        }
    }

    private fun showEmptyState() {
        binding.problemsImage.visibility = View.VISIBLE
        binding.searchPlaceholderText.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun showPlaylists() {
        binding.problemsImage.visibility = View.GONE
        binding.searchPlaceholderText.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun clearScreen() {
        binding.problemsImage.visibility = View.GONE
        binding.searchPlaceholderText.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
    }
}
