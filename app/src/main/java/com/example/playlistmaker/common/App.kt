package com.example.playlistmaker.common

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.common.data.ThemeSettings
import com.example.playlistmaker.common.di.repositoryModule
import com.example.playlistmaker.settings.data.SettingsRepository
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App  : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(repositoryModule)
        }
        val settings: SettingsRepository by inject()
        when (settings.getThemeSettings()) {
            ThemeSettings.Dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            ThemeSettings.Light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) // или другой режим по умолчанию
        }
    }
}