package com.example.playlistmaker.settings.data

import com.example.playlistmaker.common.domain.ThemeRepository
import com.example.playlistmaker.common.domain.ThemeSettings

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
            is ThemeSettings.Dark -> themeRepository.saveTheme(true)
            is ThemeSettings.Light -> themeRepository.saveTheme(false)
            else -> throw IllegalArgumentException("Unsupported theme setting")
        }
    }

    override fun getPreferredThemeMode(): Int {
        return themeRepository.getPreferredThemeMode()
    }
}
