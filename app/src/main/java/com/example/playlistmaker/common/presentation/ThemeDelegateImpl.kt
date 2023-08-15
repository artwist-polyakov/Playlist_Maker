package com.example.playlistmaker.common.presentation

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.common.data.ThemeSettings
import com.example.playlistmaker.common.domain.ThemeInteractor

class ThemeDelegateImpl(private val interactor: ThemeInteractor): ThemeDelegate {

    override fun updateTheme() {
        val theme = interactor.getTemeSettings()
        when (theme) {
            ThemeSettings.Light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ThemeSettings.Dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

}