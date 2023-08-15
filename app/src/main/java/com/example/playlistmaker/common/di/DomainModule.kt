package com.example.playlistmaker.common.di

import com.example.playlistmaker.common.domain.ThemeInteractor
import com.example.playlistmaker.common.domain.ThemeInteractorImpl
import com.example.playlistmaker.common.presentation.ThemeDelegateImpl
import com.example.playlistmaker.common.presentation.models.ThemeDelegate
import org.koin.dsl.module


val domainModule = module {
    single<ThemeInteractor> { ThemeInteractorImpl(get(), get()) }
    single<ThemeDelegate> { ThemeDelegateImpl(get()) }
}