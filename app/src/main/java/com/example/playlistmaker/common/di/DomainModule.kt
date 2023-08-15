package com.example.playlistmaker.common.di

import com.example.playlistmaker.common.domain.ThemeInteractor
import com.example.playlistmaker.common.domain.ThemeInteractorImpl
import com.example.playlistmaker.common.presentation.ThemeDelegateImpl
import com.example.playlistmaker.common.presentation.ThemeDelegate
import com.example.playlistmaker.main.domain.ThemeUseCase
import com.example.playlistmaker.settings.domain.ThemeUseCaseImpl
import org.koin.dsl.module


val domainModule = module {
    single<ThemeInteractor> { ThemeInteractorImpl(get()) }
    single<ThemeDelegate> { ThemeDelegateImpl(get()) }
    single<ThemeUseCase> {ThemeUseCaseImpl(get())}
}