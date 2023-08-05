package com.example.playlistmaker.settings.data

import com.example.playlistmaker.common.data.ThemeRepository
import com.example.playlistmaker.common.data.ThemeSettings

class SettingsRepositoryImpl(private val themeRepository: ThemeRepository) : SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return if (themeRepository.isDarkTheme()) {
            ThemeSettings.Dark
        } else {
            ThemeSettings.Light
        }
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        when (settings) {
            is ThemeSettings.Dark -> themeRepository.switchTheme(true)
            is ThemeSettings.Light -> themeRepository.switchTheme(false)
            else -> throw IllegalArgumentException("Unsupported theme setting")
        }
    }
}
