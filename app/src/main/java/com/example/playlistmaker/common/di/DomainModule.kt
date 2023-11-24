package com.example.playlistmaker.common.di

import com.example.playlistmaker.common.data.ImagesRepositoryInteractorImpl
import com.example.playlistmaker.common.domain.db.PlaylistsDbInteractorImpl
import com.example.playlistmaker.common.domain.ThemeInteractor
import com.example.playlistmaker.common.data.ThemeInteractorImpl
import com.example.playlistmaker.common.data.ThemeDelegateImpl
import com.example.playlistmaker.common.domain.db.PlaylistsDbInteractor
import com.example.playlistmaker.common.domain.db.TracksDbInteractor
import com.example.playlistmaker.common.domain.db.TracksDbInteractorImpl
import com.example.playlistmaker.common.presentation.ThemeDelegate
import com.example.playlistmaker.media.data.ImagesStorageInteractorImpl
import com.example.playlistmaker.media.domain.ImagesRepositoryInteractor
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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    singleOf(::ThemeInteractorImpl) bind ThemeInteractor::class
    singleOf(::ThemeDelegateImpl) bind ThemeDelegate::class
    singleOf(::ThemeUseCaseImpl) bind ThemeUseCase::class
    singleOf(::SettingsInteractorImpl) bind SettingsInteractor::class
    singleOf(::NavigationInteractor) bind NavigationInteractor::class
    singleOf(::TracksInteractorImpl) bind TracksInteractor::class
    singleOf(::TrackStorageInteractorImpl) bind TrackStorageInteractor::class
    singleOf(::MediaPlayerInteractorImpl) bind MediaPlayerInteractor::class
    singleOf(::TracksDbInteractorImpl) bind TracksDbInteractor::class
    singleOf(::ImagesStorageInteractorImpl) bind ImagesStorageInteractor::class
    singleOf(::PlaylistsDbInteractorImpl) bind PlaylistsDbInteractor::class
    singleOf(::ImagesRepositoryInteractorImpl) bind ImagesRepositoryInteractor::class
}