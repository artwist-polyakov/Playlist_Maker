package com.example.playlistmaker.common.di

import com.example.playlistmaker.common.data.PlaylistsDbRepositoryImpl
import com.example.playlistmaker.common.data.ThemeRepositoryImpl
import com.example.playlistmaker.common.data.TracksDbRepositoryImpl
import com.example.playlistmaker.common.data.converters.PlaylistsDbConverter
import com.example.playlistmaker.common.data.converters.TracksDbConvertor
import com.example.playlistmaker.common.domain.ThemeRepository
import com.example.playlistmaker.common.domain.db.PlaylistsDbRepository
import com.example.playlistmaker.common.domain.db.TracksDbRepository
import com.example.playlistmaker.player.data.MediaPlayerImpl
import com.example.playlistmaker.player.domain.MediaPlayerInterface
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.storage.TracksStorage
import com.example.playlistmaker.search.data.storage.TracksStorageImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<ThemeRepository> { ThemeRepositoryImpl( get() ) }
    single<SettingsRepository> { SettingsRepositoryImpl( get() ) }
    single<TracksRepository> { TracksRepositoryImpl( get(), get() ) }
    single<TracksStorage>{ TracksStorageImpl( get() ) }
    single<NetworkClient> { RetrofitNetworkClient( get() ) }
    factory<MediaPlayerInterface> { MediaPlayerImpl() }
    factory { TracksDbConvertor() }
    factory { PlaylistsDbConverter() }
    single <TracksDbRepository> { TracksDbRepositoryImpl( get(), get() ) }
    single <PlaylistsDbRepository> { PlaylistsDbRepositoryImpl( get(), get(), get() ) }
}
