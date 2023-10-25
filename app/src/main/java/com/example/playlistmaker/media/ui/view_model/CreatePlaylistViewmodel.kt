package com.example.playlistmaker.media.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.domain.ImagesRepository
import com.example.playlistmaker.media.ui.view_model.states.PlaylistInputData

class CreatePlaylistViewmodel (
    private val imagesRepository: ImagesRepository
): ViewModel() {

    private var currentInputData = PlaylistInputData()

    private val _buttonState = MutableLiveData<Boolean>()
    val buttonState: LiveData<Boolean> get() = _buttonState

    init {
        _buttonState.value = false
        imagesRepository.clearAllImages()
    }

    fun setName(name: String) {
        currentInputData = currentInputData.copy(title = name)
        checkButtonState()
    }

    private fun checkButtonState() {
        _buttonState.value = !currentInputData.title.isNullOrEmpty()
    }

    fun setDescription(description: String) {
        currentInputData = currentInputData.copy(description = description)
        checkButtonState()
    }

    fun setImage(image: Uri) {
        currentInputData = currentInputData.copy(image = image)
        checkButtonState()
    }

    fun saveData() {
        currentInputData.image?.let {
            imagesRepository.saveImage(it)
        }
    }
}