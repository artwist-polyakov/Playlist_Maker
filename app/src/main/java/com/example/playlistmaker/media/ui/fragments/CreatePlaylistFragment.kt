package com.example.playlistmaker.media.ui.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewmodel
import com.example.playlistmaker.media.ui.view_model.states.CreatePlaylistScreenState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment: Fragment() {
    private val viewModel: CreatePlaylistViewmodel by viewModel()
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

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    binding.imageView.setImageURI(uri)
                    binding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP

                    viewModel.setImage(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.button.isEnabled = false
        binding.titleField.addTextChangedListener {
            viewModel.setName(it.toString())
        }
        binding.descriptionField.addTextChangedListener {
            viewModel.setDescription(it.toString())
        }
        binding.button.setOnClickListener {
            Toast.makeText(context, "Playlist created", Toast.LENGTH_SHORT).show()
            viewModel.saveData()
        }

        binding.returnButton.setOnClickListener {
            viewModel.handleExit()
        }

//        viewModel.buttonState.observe(viewLifecycleOwner) {
//            binding.button.isEnabled = it
//        }

        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }

        binding.imageView.setOnClickListener(View.OnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        })
    }

    private fun render(state: CreatePlaylistScreenState) {
        when (state) {
            is CreatePlaylistScreenState.ReadyToSave -> {
                binding.button.isEnabled = true
            }
            is CreatePlaylistScreenState.NotReadyToSave -> {
                binding.button.isEnabled = false
            }
            is CreatePlaylistScreenState.ShowPopupConfirmation -> {
                Log.d("CreatePlaylistViewmodel", "render: ShowPopupConfirmation")
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Завершить создание плейлиста?")
                    .setMessage("Все несохраненные данные будут потеряны")
                    .setNegativeButton("Отмена") { dialog, which ->
                        viewModel.continueCreation()
                    }
                    .setPositiveButton("Завершить") { dialog, which ->
                        viewModel.clearInputData()
                        viewModel.handleExit()
                    }
                    .show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            }
            is CreatePlaylistScreenState.GoodBye -> {
                findNavController().popBackStack()
            }
            is CreatePlaylistScreenState.BasicState -> {
                Log.d("CreatePlaylistFragment", "render: BasicState")
            }
        }
    }
}