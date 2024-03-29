package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.domain.ThemeInteractor
import com.example.playlistmaker.common.domain.ThemeSettings
import com.example.playlistmaker.settings.data.SettingsRepository

class ThemeInteractorImpl(private val settingsRepository: SettingsRepository) : ThemeInteractor {
    override fun getTemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun getPreferredThemeMode(): Int {
        return settingsRepository.getPreferredThemeMode()
    }
}