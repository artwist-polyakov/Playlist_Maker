package com.example.playlistmaker.media.ui.view_model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.db.PlaylistsDbInteractor
import com.example.playlistmaker.media.domain.ImagesRepositoryInteractor
import com.example.playlistmaker.media.ui.view_model.models.PlaylistInputData
import com.example.playlistmaker.media.ui.view_model.states.CreatePlaylistScreenState
import kotlinx.coroutines.launch

class CreatePlaylistViewmodel (
    private val imagesInteractor: ImagesRepositoryInteractor,
    private val playlistsDb: PlaylistsDbInteractor
): ViewModel() {

    private var currentInputData = PlaylistInputData()

    private val _state = MutableLiveData<CreatePlaylistScreenState>()
    val state: LiveData<CreatePlaylistScreenState> get() = _state

    init {
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
        when (currentInputData.title.isNullOrEmpty()) {
            true -> _state.postValue(CreatePlaylistScreenState.NotReadyToSave)
            false -> _state.postValue(CreatePlaylistScreenState.ReadyToSave)
        }
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
        _state.postValue(CreatePlaylistScreenState.GoodBye)
    }

    fun handleExit() {
        Log.d("CreatePlaylistViewmodel", "handleExit: ${currentInputData.isDataEntered()}")
        when (currentInputData.isDataEntered()) {
            true -> _state.postValue(CreatePlaylistScreenState.ShowPopupConfirmation)
            false -> _state.postValue(CreatePlaylistScreenState.GoodBye)
        }
    }

    fun clearInputData() {
        currentInputData = PlaylistInputData()
        checkButtonState()
    }

    fun continueCreation() {
        _state.postValue(CreatePlaylistScreenState.BasicState)
    }

}