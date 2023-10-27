package com.example.playlistmaker.media.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewmodel
import com.example.playlistmaker.media.ui.view_model.models.CreatePlaylistData
import com.example.playlistmaker.media.ui.view_model.states.CreatePlaylistScreenInteraction
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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.handleExit()
        }
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    binding.imageView.setImageURI(uri)
                    binding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    val data = CreatePlaylistData.ImageUri(uri)
                    viewModel.handleInteraction(CreatePlaylistScreenInteraction.DataFilled(data))
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.button.isEnabled = false
        binding.titleField.addTextChangedListener {
            val data = CreatePlaylistData.Title(it.toString())
            viewModel.handleInteraction(CreatePlaylistScreenInteraction.DataFilled(data))
        }
        binding.descriptionField.addTextChangedListener {
            val data = CreatePlaylistData.Description(it.toString())
            viewModel.handleInteraction(CreatePlaylistScreenInteraction.DataFilled(data))
        }
        binding.button.setOnClickListener {
            viewModel.handleInteraction(CreatePlaylistScreenInteraction.SaveButtonPressed)
        }

        binding.returnButton.setOnClickListener {
            viewModel.handleInteraction(CreatePlaylistScreenInteraction.BackButtonPressed)
        }

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
                showExitConfirmation()
            }
            is CreatePlaylistScreenState.GoodBye -> {
                findNavController().popBackStack()
            }
            is CreatePlaylistScreenState.BasicState -> {
                Log.d("CreatePlaylistFragment", "render: BasicState")
            }
        }
    }

    private fun showExitConfirmation() {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true)
        val colorOnPrimary = typedValue.data

        val likeColor = ContextCompat.getColor(requireContext(), R.color.like_color)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setPositiveButton("Отмена") { dialog, which ->
                viewModel.continueCreation()
            }
            .setNegativeButton("Завершить") { dialog, which ->
                viewModel.clearInputData()
                viewModel.handleExit()
            }
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorOnPrimary)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(likeColor)
    }
}