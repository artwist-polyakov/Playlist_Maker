package com.example.playlistmaker.common.di

import com.example.playlistmaker.main.ui.view_model.MainViewModel
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {MainViewModel()}
    viewModel {SettingsViewModel(get(), get(), get())}
}