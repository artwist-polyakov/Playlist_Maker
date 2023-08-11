package com.example.playlistmaker.main.ui.view_model

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.main.domain.ThemeUseCase
import com.example.playlistmaker.media.ui.activity.MediaActivity
import com.example.playlistmaker.search.ui.activity.SearchActivity
import com.example.playlistmaker.settings.ui.activity.SettingsActivity

class MainViewModel(
    application: Application,
    themeUseCase: ThemeUseCase
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val themeUseCase = Creator.provideThemeUseCase(application)
                MainViewModel(application, themeUseCase)
            }
        }
    }
    private val _activityTarget = MutableLiveData<Intent>()
    val activityTarget: LiveData<Intent> get() = _activityTarget

    private val _themeSwitch = MutableLiveData<Boolean>()

    val themeSwitch: LiveData<Boolean> get() = _themeSwitch
    private val _themeUseCase: ThemeUseCase get() = _themeUseCase

    init {
        _themeSwitch.value = themeUseCase.isDarkTheme()
    }

    fun onSearchClicked() {
        val intent = Intent(getApplication(), SearchActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        _activityTarget.value = intent
    }

    fun onMediaClicked() {
        val intent = Intent(getApplication(), MediaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        _activityTarget.value = intent
    }

    fun onSettingsClicked() {
        val intent = Intent(getApplication(), SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        _activityTarget.value = intent
    }

    fun switchTheme(isDark: Boolean) {
        _themeSwitch.value = isDark
    }

}
