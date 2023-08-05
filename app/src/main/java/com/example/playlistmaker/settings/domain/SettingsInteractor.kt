package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.common.data.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)

    fun getShareLink(): String
    fun getSupportEmail(): String
    fun getAgreementLink(): String
    fun getSupportSubject(): String
}