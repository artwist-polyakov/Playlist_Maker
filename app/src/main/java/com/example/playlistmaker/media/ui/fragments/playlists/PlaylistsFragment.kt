package com.example.playlistmaker.media.ui.fragments.playlists

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.media.ui.view_model.states.PlaylistsScreenState
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private val viewModel: PlaylistsViewModel by viewModel()
    companion object {
        fun newInstance() = PlaylistsFragment()
        private const val CLICK_DEBOUNCE_DELAY = 10L
    }

    private var onPlaylistClickDebounce: (Track) -> Unit = {}

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = Grid
        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }

    }

    private fun render(state: PlaylistsScreenState) {
        when (state) {
            is PlaylistsScreenState.GoToPlaylist -> {
                Log.d("PlaylistsFragment", "GoToPlaylist ${state.playlist}")
            }
            is PlaylistsScreenState.Content -> {
                showPlaylists()

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
}
