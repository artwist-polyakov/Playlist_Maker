package com.example.playlistmaker.media.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    // TODO Позднее здесь будет ViewModel
    private lateinit var binding: FragmentPlaylistsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
        // TODO Обработка кнопки создания плейлиста и другой логики будет здесь.
    }
}
