package com.example.playlistmaker.settings.ui.view_model

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.common.data.ThemeSettings
import com.example.playlistmaker.settings.domain.EmailData
import com.example.playlistmaker.common.domain.models.SingleLiveEvent
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.ExternalNavigator
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel.ExternalEvent.*

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val externalNavigator: ExternalNavigator,
) : ViewModel() {
    enum class ExternalEvent {
        SHARE, LINK, EMAIL
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                val settingsInteractor = Creator.provideSettingsInteractor(application)
                val externalNavigator = Creator.provideExternalNavigator(application)
                SettingsViewModel(settingsInteractor, externalNavigator)
            }
        }

        const val DEBOUNCE_TIME_500L = 500L
    }
    val restartActivity = SingleLiveEvent<Unit>()
    val closeScreen = SingleLiveEvent<Unit>()
    private val _navigateTo = MutableLiveData<Intent>()
    val navigateTo: LiveData<Intent> get() = _navigateTo
    private val _externalEvent = MutableLiveData<ExternalEvent>()
    val externalEvent: LiveData<ExternalEvent> get() = _externalEvent
    val isDarkTheme = MutableLiveData<Boolean>()
    val themeSwitcherEnabled = MutableLiveData<Boolean>(true)

    private val themeSwitchHandler = Handler(Looper.getMainLooper())

    init {
        val themeSettings = settingsInteractor.getThemeSettings()
        isDarkTheme.value = themeSettings is ThemeSettings.Dark
    }

    fun onBackClicked() {
        closeScreen.value = Unit
    }

    fun onThemeSwitch(isChecked: Boolean) {

        if (!themeSwitcherEnabled.value!!) {
            return
        }
        themeSwitcherEnabled.value = false

        val newThemeSettings = if (isChecked) ThemeSettings.Dark else ThemeSettings.Light
        settingsInteractor.updateThemeSetting(newThemeSettings)
        isDarkTheme.value = isChecked

        if (newThemeSettings == ThemeSettings.Dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        restartActivity.value = Unit

        themeSwitchHandler.postDelayed({
            themeSwitcherEnabled.value = true
        }, DEBOUNCE_TIME_500L)

    }

    fun shareLink() {
        val link = settingsInteractor.getShareLink()
        _navigateTo.value = externalNavigator.shareLink(link)
    }

    fun sendSupport() {
        val emailData = EmailData(
            settingsInteractor.getSupportEmail(),
            settingsInteractor.getSupportSubject(),
            ""
        )
        _navigateTo.value = externalNavigator.openEmail(emailData)
    }

    fun openAgreement() {
        val link = settingsInteractor.getAgreementLink()
        _navigateTo.value = externalNavigator.openLink(link)
    }

    fun onNavigationEvent(externalEvent: ExternalEvent) {
        when (externalEvent) {
            SHARE -> {
                _externalEvent.value = SHARE
            }
            LINK -> {
                _externalEvent.value = LINK
            }
            EMAIL -> {
                _externalEvent.value = EMAIL
            }
        }
    }

    fun onShareClicked() {
        onNavigationEvent(SHARE)
    }

    fun onSupportClicked() {
        onNavigationEvent(EMAIL)
    }

    fun onAgreementClicked() {
        onNavigationEvent(LINK)
    }


}
