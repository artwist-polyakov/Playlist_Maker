package com.example.playlistmaker.common.data

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.common.domain.ThemeInteractor
import com.example.playlistmaker.common.presentation.ThemeDelegate

class ThemeDelegateImpl(private val interactor: ThemeInteractor) : ThemeDelegate {

    override fun updateTheme() {
        val themeMode = interactor.getPreferredThemeMode()
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
}
