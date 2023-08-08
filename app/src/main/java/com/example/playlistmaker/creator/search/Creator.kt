package com.example.playlistmaker.creator.search

import android.content.Context
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.player.presentation.PlayerActivityInterface
import com.example.playlistmaker.player.presentation.PlayerPresenter
import com.example.playlistmaker.player.ui.view_model.PlayerView
import com.example.playlistmaker.search.api.ITunesRepository
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.search.data.storage.TracksStorage
import com.example.playlistmaker.search.data.storage.TracksStorageImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.models.Track

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

    fun providePlayerPresenter(
        playerView: PlayerActivityInterface,
        track: TrackInformation
    ): PlayerPresenter {
        return PlayerPresenter(playerView, track)
    }
}