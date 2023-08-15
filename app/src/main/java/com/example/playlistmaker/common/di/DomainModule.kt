package com.example.playlistmaker.common.di

import com.example.playlistmaker.common.domain.ThemeInteractor
import com.example.playlistmaker.common.domain.ThemeInteractorImpl
import org.koin.dsl.module


val domainModule = module {
    factory<ThemeInteractor> { ThemeInteractorImpl(get()) }
}