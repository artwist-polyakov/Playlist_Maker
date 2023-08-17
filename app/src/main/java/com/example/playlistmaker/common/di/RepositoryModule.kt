package com.example.playlistmaker.common.di

import android.content.SharedPreferences
import com.example.playlistmaker.common.data.ThemeRepositoryImpl
import com.example.playlistmaker.common.domain.api.ThemeRepository
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.search.data.storage.TracksStorage
import com.example.playlistmaker.search.data.storage.TracksStorageImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.ExternalNavigator
import org.koin.dsl.module

val repositoryModule = module {
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<TracksRepository> {TracksRepositoryImpl(get(), get())}
    single<TracksStorage>{TracksStorageImpl(get())}
    single<NetworkClient> { RetrofitNetworkClient(get()) }

}

val dataModule = module {
    single<ExternalNavigator> { ExternalNavigatorImpl(get()) }

}