package com.example.playlistmaker.settings.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.data.ThemeSettings
import com.example.playlistmaker.common.domain.models.SingleLiveEvent
import com.example.playlistmaker.common.presentation.ThemeDelegate
import com.example.playlistmaker.settings.domain.NavigationInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val navigationInteractor: NavigationInteractor,
    private val delegate: ThemeDelegate
) : ViewModel() {

    companion object {
        const val DEBOUNCE_TIME_500L = 500L
    }

    val restartActivity = SingleLiveEvent<Unit>()
    val closeScreen = SingleLiveEvent<Unit>()
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

        // Чтобы нельзя было тысячу раз в миллисекунду двинуть переключатель
        if (!themeSwitcherEnabled.value!!) {
            return
        }

        // Делаем переключатель неактивным на 500 мс
        themeSwitcherEnabled.value = false
        val newThemeSettings = if (isChecked) ThemeSettings.Dark else ThemeSettings.Light
        settingsInteractor.updateThemeSetting(newThemeSettings)
        isDarkTheme.value = isChecked
        delegate.updateTheme()

        restartActivity.value = Unit
        themeSwitchHandler.postDelayed({

            // Возвращаем переключатель в активное состояние
            themeSwitcherEnabled.value = true
        }, DEBOUNCE_TIME_500L)

    }

    fun shareLink() {
        val link = settingsInteractor.getShareLink()
        navigationInteractor.navigateToShare(link)
    }

    fun sendSupport() {
        val email = settingsInteractor.getSupportEmailDate()
        navigationInteractor.navigateToSupport(email)
    }

    fun openAgreement() {
        val link = settingsInteractor.getAgreementLink()
        navigationInteractor.navigateToAgreement(link)
    }
}
