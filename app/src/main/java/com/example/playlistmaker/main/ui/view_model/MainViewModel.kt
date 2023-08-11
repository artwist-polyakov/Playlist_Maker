package com.example.playlistmaker.main.ui.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.common.domain.models.SingleLiveEvent
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.main.domain.ThemeUseCase

class MainViewModel(
    themeUseCase: ThemeUseCase
): ViewModel()   {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                val themeUseCase = Creator.provideThemeUseCase(application)
                MainViewModel(themeUseCase)
            }
        }
    }

    enum class NavigationEvent {
        SEARCH, MEDIA, SETTINGS
    }

    private val _navigationEvent = SingleLiveEvent<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> get() = _navigationEvent
    private val _themeSwitch = MutableLiveData<Boolean>()
    val themeSwitch: LiveData<Boolean> get() = _themeSwitch

    init {
        _themeSwitch.value = themeUseCase.isDarkTheme()
    }

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
