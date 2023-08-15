package com.example.playlistmaker.common.domain

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.common.data.ThemeSettings
import com.example.playlistmaker.common.presentation.models.ThemeDelegate
import com.example.playlistmaker.settings.data.SettingsRepository

class ThemeInteractorImpl(private val settingsRepository: SettingsRepository, private val delegate: ThemeDelegate): ThemeInteractor {

    override fun updateTheme(context: Context) {
        val themeSettings = settingsRepository.getThemeSettings()
        delegate.updateTheme(themeSettings)
    }

}