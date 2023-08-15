package com.example.playlistmaker.common

import android.app.Application
import com.example.playlistmaker.common.di.domainModule
import com.example.playlistmaker.common.di.repositoryModule
import com.example.playlistmaker.common.presentation.ThemeDelegate
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App  : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(repositoryModule, domainModule)
        }
        val themeDelegate: ThemeDelegate by inject()
        themeDelegate.updateTheme()
    }
}