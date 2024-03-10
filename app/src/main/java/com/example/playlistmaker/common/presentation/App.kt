package com.example.playlistmaker.common.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.playlistmaker.common.di.dataModule
import com.example.playlistmaker.common.di.domainModule
import com.example.playlistmaker.common.di.repositoryModule
import com.example.playlistmaker.common.di.uimodule
import com.example.playlistmaker.common.di.viewModelModule
import com.example.playlistmaker.player.data.MediaPlayerService
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.Locale


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        setRussianLocale()

        startKoin {
            androidContext(this@App)
            modules(repositoryModule, domainModule, viewModelModule, dataModule, uimodule)
        }

        val themeDelegate: ThemeDelegate by inject()
        themeDelegate.updateTheme()
        val channel = NotificationChannel(
            MediaPlayerService.NOTIFICATION_CHANNEL_ID,
            "Media player notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun setRussianLocale() {
        val locale = Locale("ru")
        Locale.setDefault(locale)
        baseContext.resources.configuration.setLocale(locale)
    }
}