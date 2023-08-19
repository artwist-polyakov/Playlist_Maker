package com.example.playlistmaker.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.domain.SingleLiveEvent

class MainViewModel: ViewModel()   {
    enum class NavigationEvent {
        SEARCH, MEDIA, SETTINGS
    }
    private val _navigationEvent = SingleLiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> get() = _navigationEvent

    fun onSearchClicked() {
        _navigationEvent.value = NavigationEvent.SEARCH
    }

    fun onMediaClicked() {
        _navigationEvent.value = NavigationEvent.MEDIA
    }

    fun onSettingsClicked() {
        _navigationEvent.value = NavigationEvent.SETTINGS
    }
}
