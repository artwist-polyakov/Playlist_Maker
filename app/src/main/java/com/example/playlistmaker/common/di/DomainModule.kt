package com.example.playlistmaker.common.di

import com.example.playlistmaker.common.data.PlaylistsDbInteractorImpl
import com.example.playlistmaker.common.domain.ThemeInteractor
import com.example.playlistmaker.common.data.ThemeInteractorImpl
import com.example.playlistmaker.common.data.ThemeDelegateImpl
import com.example.playlistmaker.common.domain.db.PlaylistsDbInteractor
import com.example.playlistmaker.common.domain.db.TracksDbInteractor
import com.example.playlistmaker.common.domain.db.TracksDbInteractorImpl
import com.example.playlistmaker.common.presentation.ThemeDelegate
import com.example.playlistmaker.media.data.ImagesStorageInteractorImpl
import com.example.playlistmaker.media.domain.ImagesStorageInteractor
import com.example.playlistmaker.player.data.MediaPlayerInteractorImpl
import com.example.playlistmaker.player.data.TrackStorageInteractorImpl
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.TrackStorageInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.NavigationInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeUseCase
import com.example.playlistmaker.settings.domain.ThemeUseCaseImpl
import org.koin.dsl.module

val domainModule = module {
    single<ThemeInteractor> { ThemeInteractorImpl( get() ) }
    single<ThemeDelegate> { ThemeDelegateImpl( get() ) }
    single<ThemeUseCase> { ThemeUseCaseImpl( get() ) }
    single<SettingsInteractor> { SettingsInteractorImpl( get(), get() ) }
    single<NavigationInteractor> { NavigationInteractor( get() ) }
    single<TracksInteractor> { TracksInteractorImpl( get() ) }
    single<TrackStorageInteractor> { TrackStorageInteractorImpl( get() ) }
    factory<MediaPlayerInteractor>{ MediaPlayerInteractorImpl( get() ) }
    single<TracksDbInteractor> { TracksDbInteractorImpl( get(), get() ) }
    single<ImagesStorageInteractor> { ImagesStorageInteractorImpl( get() ) }
    single<PlaylistsDbInteractor> { PlaylistsDbInteractorImpl( get() ) }
}