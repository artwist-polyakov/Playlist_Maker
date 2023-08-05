package com.example.playlistmaker.settings.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.common.data.ThemeSettings
import com.example.playlistmaker.common.domain.models.EmailData
import com.example.playlistmaker.common.domain.models.SingleLiveEvent
import com.example.playlistmaker.settings.data.ExternalNavigator
import com.example.playlistmaker.settings.domain.SettingsInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val externalNavigator: ExternalNavigator,
) : ViewModel() {



    companion object {
        fun getViewModelFactory(
            settingsInteractor: SettingsInteractor,
            externalNavigator: ExternalNavigator
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(settingsInteractor, externalNavigator)
            }
        }

        const val DEBOUNCE_TIME_500L = 500L
    }
    val restartActivity = SingleLiveEvent<Unit>()
    val closeScreen = SingleLiveEvent<Unit>()
    val shareLink = SingleLiveEvent<String>()
    val openLink = SingleLiveEvent<String>()
    val sendEmail = SingleLiveEvent<EmailData>()
    val isDarkTheme = MutableLiveData<Boolean>()
    val themeSwitcherEnabled = MutableLiveData<Boolean>(true)

    private val themeSwitchHandler = Handler(Looper.getMainLooper())

    init {
        // Получаем текущие настройки темы и устанавливаем значение для isDarkTheme
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

    fun onShareClicked() {
        shareLink.value = settingsInteractor.getShareLink()
    }

    fun onSupportClicked() {
        val emailData = EmailData(
            settingsInteractor.getSupportEmail(),
            settingsInteractor.getSupportSubject(),
            ""
        )
        sendEmail.value = emailData
    }

    fun onAgreementClicked() {
        openLink.value = settingsInteractor.getAgreementLink()
    }
}
