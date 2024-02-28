package com.example.playlistmaker.common.di

import com.example.playlistmaker.common.data.PlaylistsDbRepositoryImpl
import com.example.playlistmaker.common.data.PrettifyDbRepositoryImpl
import com.example.playlistmaker.common.data.ThemeRepositoryImpl
import com.example.playlistmaker.common.data.TracksDbRepositoryImpl
import com.example.playlistmaker.common.data.converters.PlaylistsDbConverter
import com.example.playlistmaker.common.data.converters.TracksDbConvertor
import com.example.playlistmaker.common.domain.ThemeRepository
import com.example.playlistmaker.common.domain.db.PlaylistsDbRepository
import com.example.playlistmaker.common.domain.db.PrettifyDbRepository
import com.example.playlistmaker.common.domain.db.TracksDbRepository
import com.example.playlistmaker.player.data.MediaPlayerImpl
import com.example.playlistmaker.player.domain.MediaPlayerInterface
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.search.data.storage.TracksStorageImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.storage.TracksStorage
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::ThemeRepositoryImpl) bind ThemeRepository::class
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class
    singleOf(::TracksRepositoryImpl) bind TracksRepository::class
    singleOf(::TracksStorageImpl) bind TracksStorage::class
    singleOf(::RetrofitNetworkClient) bind NetworkClient::class
    factoryOf(::MediaPlayerImpl) bind MediaPlayerInterface::class
    factoryOf(::TracksDbConvertor) bind TracksDbConvertor::class
    factoryOf(::PlaylistsDbConverter) bind PlaylistsDbConverter::class
    singleOf(::TracksDbRepositoryImpl) bind TracksDbRepository::class
    singleOf(::PrettifyDbRepositoryImpl) bind PrettifyDbRepository::class
    singleOf(::PlaylistsDbRepositoryImpl) bind PlaylistsDbRepository::class
}
