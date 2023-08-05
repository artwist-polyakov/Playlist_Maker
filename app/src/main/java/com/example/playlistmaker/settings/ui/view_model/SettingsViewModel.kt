package com.example.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.common.data.ThemeSettings
import com.example.playlistmaker.common.domain.models.EmailData
import com.example.playlistmaker.common.domain.models.SingleLiveEvent
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.domain.ExternalNavigator
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

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
    }
    val closeScreen = SingleLiveEvent<Unit>()
    val shareLink = SingleLiveEvent<String>()
    val openLink = SingleLiveEvent<String>()
    val sendEmail = SingleLiveEvent<EmailData>()
    val isDarkTheme = MutableLiveData<Boolean>()

    init {
        // Получаем текущие настройки темы и устанавливаем значение для isDarkTheme
        val themeSettings = settingsInteractor.getThemeSettings()
        isDarkTheme.value = themeSettings is ThemeSettings.Dark
    }

    fun onBackClicked() {
        closeScreen.value = Unit
    }

    fun onThemeSwitch(isChecked: Boolean) {
        val newThemeSettings = if (isChecked) ThemeSettings.Dark else ThemeSettings.Light
        settingsInteractor.updateThemeSetting(newThemeSettings)
        isDarkTheme.value = isChecked
    }

    fun onShareClicked() {
        shareLink.value = "link_to_share"
    }

    fun onSupportClicked() {
        val emailData = EmailData("support_email", "support_subject", "support_text")
        sendEmail.value = emailData
    }

    fun onAgreementClicked() {
        openLink.value = "link_to_agreement"
    }
}
