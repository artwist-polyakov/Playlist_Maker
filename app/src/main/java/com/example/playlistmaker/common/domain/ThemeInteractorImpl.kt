package com.example.playlistmaker.common.domain

import com.example.playlistmaker.common.data.ThemeSettings
import com.example.playlistmaker.settings.data.SettingsRepository

class ThemeInteractorImpl(private val settingsRepository: SettingsRepository): ThemeInteractor {
    override fun getTemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }
}