package com.example.playlistmaker.media.ui.view_model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.db.PlaylistsDbInteractor
import com.example.playlistmaker.media.domain.ImagesRepositoryInteractor
import com.example.playlistmaker.media.ui.view_model.states.PlaylistInputData
import kotlinx.coroutines.launch

class CreatePlaylistViewmodel (
    private val imagesInteractor: ImagesRepositoryInteractor,
    private val playlistsDb: PlaylistsDbInteractor
): ViewModel() {

    private var currentInputData = PlaylistInputData()

    private val _buttonState = MutableLiveData<Boolean>()
    val buttonState: LiveData<Boolean> get() = _buttonState

    init {
        _buttonState.value = false
        viewModelScope.launch{
            playlistsDb
                .giveMeAllPlaylists()
                .collect {
                    Log.d("CreatePlaylistViewmodel", "init: $it")
                }
        }
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
            imagesInteractor.saveImage(it)
        }
        viewModelScope.launch {
            playlistsDb.addPlaylist(currentInputData.mapToPlaylistInformation())
        }
    }
}