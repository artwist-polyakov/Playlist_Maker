package com.example.playlistmaker.common.di

import com.example.playlistmaker.common.data.ThemeRepositoryImpl
import com.example.playlistmaker.common.domain.api.ThemeRepository
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}