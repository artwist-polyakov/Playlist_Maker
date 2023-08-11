package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.common.data.ThemeRepository
import com.example.playlistmaker.common.data.ThemeRepositoryImpl
import com.example.playlistmaker.main.domain.ThemeUseCase
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.search.data.storage.TracksStorageImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeUseCaseImpl

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

}