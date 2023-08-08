package com.example.playlistmaker.main.ui.view_model

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.common.data.ThemeRepository
import com.example.playlistmaker.media.ui.activity.MediaActivity
import com.example.playlistmaker.search.ui.activity.SearchActivity
import com.example.playlistmaker.settings.ui.activity.SettingsActivity

class MainViewModel(
    application: Application,
    private val themeRepository: ThemeRepository
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(application: Application, themeRepository: ThemeRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel(application, themeRepository)
            }
        }
    }


    private val _navigateTo = MutableLiveData<Intent>()
    val navigateTo: LiveData<Intent> get() = _navigateTo

    private val _themeSwitch = MutableLiveData<Boolean>()
    val themeSwitch: LiveData<Boolean> get() = _themeSwitch
    private val _themeRepository: ThemeRepository get() = _themeRepository

    init {
        _themeSwitch.value = themeRepository.isDarkTheme()
    }

    fun onSearchClicked() {
        val intent = Intent(getApplication(), SearchActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        _navigateTo.value = intent
    }

    fun onMediaClicked() {
        val intent = Intent(getApplication(), MediaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        _navigateTo.value = intent
    }

    fun onSettingsClicked() {
        val intent = Intent(getApplication(), SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        _navigateTo.value = intent
    }

    fun switchTheme(isDark: Boolean) {
        _themeSwitch.value = isDark
    }

}
