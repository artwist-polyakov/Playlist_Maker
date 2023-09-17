package com.example.playlistmaker.settings.data

import com.example.playlistmaker.common.domain.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
    fun getPreferredThemeMode(): Int
}