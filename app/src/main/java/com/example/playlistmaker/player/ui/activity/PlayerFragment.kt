package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.playlistmaker.common.presentation.models.TrackInformation
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PlayerFragment : Fragment() {
    private val viewModel: PlayerViewModel by viewModel()
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentTrack: TrackInformation
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }
    
}