package com.example.playlistmaker.common.domain

import android.content.Context
import com.example.playlistmaker.common.presentation.ThemeDelegate
import com.example.playlistmaker.settings.data.SettingsRepository

class ThemeInteractorImpl(private val settingsRepository: SettingsRepository, private val delegate: ThemeDelegate): ThemeInteractor {

    override fun updateTheme(context: Context) {
        val themeSettings = settingsRepository.getThemeSettings()
        delegate.updateTheme(themeSettings)
    }

}