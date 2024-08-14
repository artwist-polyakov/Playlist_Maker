package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.PlaylistMakerTheme
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.media.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.compose.koinViewModel

class MediaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val favoritesViewModel: FavoritesViewModel = koinViewModel()
                val playlistsViewModel: PlaylistsViewModel = koinViewModel()
                val onTrackClick = remember<(Track) -> Unit> {
                    { track ->
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_playerFragment,
                            Bundle().apply {
                                putParcelable("track", track)
                            }
                        )
                    }
                }

                val onPlaylistClick = remember<(PlaylistInformation) -> Unit> {
                    { playlist ->
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_playlistFragment,
                            Bundle().apply {
                                putString("playlistId", playlist.id.toString())
                            }
                        )
                    }
                }

                PlaylistMakerTheme {
                    MediaScreen(
                        favoritesViewModel = favoritesViewModel,
                        playlistsViewModel = playlistsViewModel,
                        onTrackClick = onTrackClick,
                        onPlaylistClick = onPlaylistClick
                    )
                }
            }
        }
    }
}