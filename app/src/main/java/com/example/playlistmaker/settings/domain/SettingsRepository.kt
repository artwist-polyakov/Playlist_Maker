package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.common.data.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}