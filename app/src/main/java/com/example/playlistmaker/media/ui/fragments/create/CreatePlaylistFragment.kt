package com.example.playlistmaker.media.ui.fragments.create

import android.app.AlertDialog
import android.content.res.ColorStateList
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
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.common.utils.showCustomSnackbar
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewmodel
import com.example.playlistmaker.media.ui.view_model.models.CreatePlaylistData
import com.example.playlistmaker.media.ui.view_model.states.CreatePlaylistScreenInteraction
import com.example.playlistmaker.media.ui.view_model.states.CreatePlaylistScreenState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.android.material.R as MaterialR

// TODO: Создать конструктор Instance of CreatePlaylistFragment и передать в него playlistId
// TODO: Инициализировать во ViewModel playlistId из конструктора
// TODO: Менять логику поведения при заполненном playlistId и при пустом во VoewwModel
// TODO: Удалять фотографию из хранилища при смене фотографии

class CreatePlaylistFragment : Fragment(), CreatePlaylistInterface {
    private val viewModel: CreatePlaylistViewmodel by viewModel()
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    // MARK :- Lifecycle
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

        /*
        Способ 1 — установки цвета бордера (он реже будет дёргать контекст и
         ресурсы,но очень длинный)
         */
        binding.titleField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                AppCompatResources.getColorStateList(
                    requireContext(),
                    R.color.box_stroke_color_blue
                )
                    ?.let { binding.textInputLayout1.setBoxStrokeColorStateList(it) }
            } else {
                when (binding.titleField.text.isNullOrEmpty()) {
                    true -> {
                        AppCompatResources.getColorStateList(
                            requireContext(),
                            R.color.box_stroke_color
                        )
                            ?.let {
                                binding.textInputLayout1.setBoxStrokeColorStateList(it)
                                binding.textInputLayout1.defaultHintTextColor = it
                            }
                    }

                    false -> {
                        AppCompatResources.getColorStateList(
                            requireContext(),
                            R.color.box_stroke_color_blue
                        )
                            ?.let {
                                binding.textInputLayout1.setBoxStrokeColorStateList(it)
                                binding.textInputLayout1.defaultHintTextColor = it
                            }
                    }
                }
            }
        }
        binding.button.isEnabled = false
        binding.titleField.addTextChangedListener {

            val data = CreatePlaylistData.Title(it.toString())
            viewModel.handleInteraction(CreatePlaylistScreenInteraction.DataFilled(data))
        }

        /*
        Способ 2 — установки цвета бордера (он часто будет дёргать контекст и ресурсы,
        но короткий  смотрится красиво
         */
        binding.descriptionField.addTextChangedListener {
            val colorValues = getFieldColorStateList(it.toString().isNullOrEmpty())
            binding.textInputLayout2.setBoxStrokeColorStateList(colorValues)
            binding.textInputLayout2.defaultHintTextColor = colorValues
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

        binding.imageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // MARK :- Private
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
                goBack()
//                findNavController().popBackStack()
            }

            is CreatePlaylistScreenState.BasicState -> {
                Log.d("CreatePlaylistFragment", "render: BasicState")
            }

            is CreatePlaylistScreenState.SuccessState -> {
                showSuccess(state.name)
                goBack()
            }
        }
    }

    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun showExitConfirmation() {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(MaterialR.attr.colorOnPrimary, typedValue, true)
        val colorOnPrimary = typedValue.data

        val likeColor = ContextCompat.getColor(requireContext(), R.color.like_color)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.exit_confirmation_title))
            .setMessage(getString(R.string.exit_confirmation_message))
            .setPositiveButton(getString(R.string.cancel_button)) { dialog, which ->
                viewModel.continueCreation()
            }
            .setNegativeButton(getString(R.string.finish_button)) { dialog, which ->
                viewModel.clearInputData()
                viewModel.handleExit()
            }
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorOnPrimary)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(likeColor)
    }

    private fun showSuccess(title: String) {
        binding
            .root
            .showCustomSnackbar(
                getString(
                    R.string.playlist_created,
                    title
                )
            )
    }

    override fun emulateBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.handleExit()
        }
    }

    private fun getFieldColorStateList(isEmpty: Boolean): ColorStateList {
        Log.d("ColorOfLayout", "getFieldColorAttr - $isEmpty")
        val colorRes = if (isEmpty) {
            R.color.box_stroke_color
        } else {
            R.color.box_stroke_color_blue
        }
        return AppCompatResources.getColorStateList(requireContext(), colorRes)
    }
}