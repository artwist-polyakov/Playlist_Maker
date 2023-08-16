package com.example.playlistmaker.common.di

import com.example.playlistmaker.common.data.ThemeRepositoryImpl
import com.example.playlistmaker.common.domain.api.ThemeRepository
import com.example.playlistmaker.settings.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.ExternalNavigator
import org.koin.dsl.module

val repositoryModule = module {
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}

val dataModule = module {
    single<ExternalNavigator> { ExternalNavigatorImpl(get()) }
}