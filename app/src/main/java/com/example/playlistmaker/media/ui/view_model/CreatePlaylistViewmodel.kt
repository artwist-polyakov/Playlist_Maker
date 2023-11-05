package com.example.playlistmaker.media.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.db.PlaylistsDbInteractor
import com.example.playlistmaker.common.presentation.models.PlaylistInformation
import com.example.playlistmaker.media.domain.ImagesRepositoryInteractor
import com.example.playlistmaker.media.ui.view_model.models.CreatePlaylistData
import com.example.playlistmaker.media.ui.view_model.models.PlaylistInputData
import com.example.playlistmaker.media.ui.view_model.states.CreatePlaylistScreenInteraction
import com.example.playlistmaker.media.ui.view_model.states.CreatePlaylistScreenState
import kotlinx.coroutines.launch

class CreatePlaylistViewmodel(
    private val playlistId: String,
    private val imagesInteractor: ImagesRepositoryInteractor,
    private val playlistsDb: PlaylistsDbInteractor
) : ViewModel() {

    private var currentInputData = PlaylistInputData()

    private var currentPlaylist: PlaylistInformation? = null

    private val _state = MutableLiveData<CreatePlaylistScreenState>()
    val state: LiveData<CreatePlaylistScreenState> get() = _state

    init {
        if (playlistId != "null") {
            viewModelScope.launch {
                playlistsDb.getPlaylist(playlistId).let {
                    currentPlaylist = it
                    currentInputData = currentInputData.copy(
                        title = it.name,
                        description = it.description,
                        image = it.image
                    )
                    _state.postValue(CreatePlaylistScreenState.ReadyToSave)
                    _state.postValue(
                        CreatePlaylistScreenState.ReadyToEdit(
                            name = it.name,
                            description = it.description,
                            image = it.image
                        )
                    )
                }
            }
        } else {
            _state.postValue(CreatePlaylistScreenState.BasicState)
        }
    }

    fun handleInteraction(interaction: CreatePlaylistScreenInteraction) {
        when (interaction) {
            is CreatePlaylistScreenInteraction.BackButtonPressed -> handleExit()
            is CreatePlaylistScreenInteraction.SaveButtonPressed -> saveData()
            is CreatePlaylistScreenInteraction.DataFilled -> handleDataFilled(interaction.content)
        }
    }

    private fun handleDataFilled(content: CreatePlaylistData) {
        when (content) {
            is CreatePlaylistData.Title -> currentInputData =
                currentInputData.copy(title = content.value)

            is CreatePlaylistData.Description -> currentInputData =
                currentInputData.copy(description = content.value)

            is CreatePlaylistData.ImageUri -> currentInputData =
                currentInputData.copy(image = content.value)
        }
        checkButtonState()
    }

    private fun checkButtonState() {
        when (currentInputData.title.isNullOrEmpty()) {
            true -> _state.postValue(CreatePlaylistScreenState.NotReadyToSave)
            false -> _state.postValue(CreatePlaylistScreenState.ReadyToSave)
        }
    }

    private fun saveData() {
        var imageLink: String?
        currentInputData.image?.let {
            imageLink = imagesInteractor.saveImage(it)
            Uri.parse(imageLink).let {
                currentInputData = currentInputData.copy(image = it)
            }
        }
        viewModelScope.launch {
            playlistsDb.addPlaylist(currentInputData.mapToPlaylistInformation())
        }
        _state.postValue(CreatePlaylistScreenState.SuccessState(currentInputData.title))

    }

    fun handleExit() {
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