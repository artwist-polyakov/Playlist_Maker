package com.example.playlistmaker.common.di

import android.content.Context
import com.example.playlistmaker.common.utils.ConfirmationPresenter
import com.example.playlistmaker.media.ui.view_model.CreatePlaylistViewmodel
import com.example.playlistmaker.media.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.example.playlistmaker.media.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { PlayerViewModel(get(), get(), get(), get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { FavoritesViewModel(get(), get()) }
    viewModel { CreatePlaylistViewmodel(get(), get()) }
    viewModel { (playlistId: String) -> PlaylistViewModel(playlistId = playlistId, get(), get(), get())}
}

val uimodule = module {
    factory { (context: Context) -> ConfirmationPresenter(context = context) }
}
