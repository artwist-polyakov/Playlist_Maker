package com.example.playlistmaker.search.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.InternetCheckingFragment
import com.example.playlistmaker.common.presentation.InternetConnectionBroadcastReciever
import com.example.playlistmaker.common.presentation.PlaylistMakerTheme
import com.example.playlistmaker.common.presentation.models.TrackToTrackDtoMapper
import com.example.playlistmaker.common.presentation.debounce
import com.example.playlistmaker.common.presentation.showCustomSnackbar
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class ResponseState {
    SUCCESS,
    NOTHING_FOUND,
    ERROR,
    CLEAR
}

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PlaylistMakerTheme {
                    SearchScreen(viewModel = viewModel, navController = findNavController())
                }
            }
        }
    }
}