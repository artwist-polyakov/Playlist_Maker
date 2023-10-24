package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewmodel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment: Fragment() {
    private val viewModel: CreatePlaylistViewmodel by viewModel()
//    companion object {
//        fun newInstance() = CreatePlaylistFragment()
//    }
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.isEnabled = false
        binding.titleField.addTextChangedListener {
            viewModel.setName(it.toString())
        }
        binding.descriptionField.addTextChangedListener {
            viewModel.setDescription(it.toString())
        }
        binding.button.setOnClickListener {
            Toast.makeText(context, "Playlist created", Toast.LENGTH_SHORT).show()
        }
        viewModel.buttonState.observe(viewLifecycleOwner) {
            Toast.makeText(context, "Button ready $it", Toast.LENGTH_SHORT).show()
            binding.button.isEnabled = it
        }

        binding.imageView.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "Image clicked", Toast.LENGTH_SHORT).show()
        })

    }
}