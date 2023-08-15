package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.common.domain.api.ThemeRepository
import com.example.playlistmaker.common.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeUseCase
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.search.data.storage.TracksStorageImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeUseCaseImpl
import com.example.playlistmaker.settings.domain.ExternalNavigator
import com.example.playlistmaker.settings.domain.NavigationInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl

object Creator {

    private fun getTracksRepository(context: Context): TracksRepositoryImpl {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(context),
            TracksStorageImpl(context.getSharedPreferences("local_storage", Context.MODE_PRIVATE)),
        )
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(context)
    }

    fun provideThemeUseCase(context: Context): ThemeUseCase {
        return ThemeUseCaseImpl(getThemeRepository(context))
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(getThemeRepository(context))
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context), context)
    }

    fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideNavigationInteractor(context: Context): NavigationInteractor {
        return NavigationInteractor(provideExternalNavigator(context))
    }

}