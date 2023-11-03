package com.example.playlistmaker.media.ui.fragments.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment: Fragment() {
    private val viewModel: PlaylistViewModel by viewModel { parametersOf(playlistId) }
    private var playlistId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistId = arguments?.getString(ARG_PLAYLIST_ID) ?: "null"
    }
    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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