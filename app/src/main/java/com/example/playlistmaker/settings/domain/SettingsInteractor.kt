package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.common.domain.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
    fun getAgreementLink(): String
    fun getShareLink(): String
    fun getSupportEmail(): String
    fun getSupportEmailDate(): EmailData
}