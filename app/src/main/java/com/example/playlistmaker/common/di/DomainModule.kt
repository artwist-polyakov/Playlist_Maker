package com.example.playlistmaker.common.di

import com.example.playlistmaker.common.domain.ThemeInteractor
import com.example.playlistmaker.common.domain.ThemeInteractorImpl
import com.example.playlistmaker.common.presentation.ThemeDelegateImpl
import com.example.playlistmaker.common.presentation.ThemeDelegate
import com.example.playlistmaker.player.data.TrackStorageInteractorImpl
import com.example.playlistmaker.player.domain.MediaPlayerCallbackInterface
import com.example.playlistmaker.player.domain.TrackStorageInteractor
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.NavigationInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeUseCase
import com.example.playlistmaker.settings.domain.ThemeUseCaseImpl
import org.koin.dsl.module


val domainModule = module {
    single<ThemeInteractor> { ThemeInteractorImpl(get()) }
    single<ThemeDelegate> { ThemeDelegateImpl(get()) }
    single<ThemeUseCase> {ThemeUseCaseImpl(get())}
    single<SettingsInteractor> { SettingsInteractorImpl(get(), get()) }
    single<NavigationInteractor> { NavigationInteractor(get()) }
    single<TracksInteractor> {TracksInteractorImpl(get())}
    single<TrackStorageInteractor> { TrackStorageInteractorImpl(get()) }
    single<MediaPlayerCallbackInterface> {PlayerViewModel()}
}