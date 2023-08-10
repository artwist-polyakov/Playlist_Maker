package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.common.data.ThemeRepository
import com.example.playlistmaker.common.data.ThemeSettings
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val themeRepository = ThemeRepository(applicationContext)
        val settingsRepositoryImpl = SettingsRepositoryImpl(themeRepository)
        val currentTheme = settingsRepositoryImpl.getThemeSettings()

        when (currentTheme) {
            ThemeSettings.Dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            ThemeSettings.Light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) // или другой режим по умолчанию
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}