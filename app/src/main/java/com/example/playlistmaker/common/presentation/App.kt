package com.example.playlistmaker.common.presentation

import android.app.Application
import android.content.res.Configuration
import com.example.playlistmaker.common.di.dataModule
import com.example.playlistmaker.common.di.domainModule
import com.example.playlistmaker.common.di.repositoryModule
import com.example.playlistmaker.common.di.uimodule
import com.example.playlistmaker.common.di.viewModelModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.Locale

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        tyRusskiy()

        startKoin {
            androidContext(this@App)
            modules(repositoryModule, domainModule, viewModelModule, dataModule, uimodule)
        }

        val themeDelegate: ThemeDelegate by inject()
        themeDelegate.updateTheme()
    }

    private fun tyRusskiy() {
        val locale = Locale("ru")
        Locale.setDefault(locale)
        val config: Configuration = baseContext.resources.configuration
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
    }
}