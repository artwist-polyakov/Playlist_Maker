package com.example.playlistmaker.creator.search

import android.content.Context
import com.example.playlistmaker.player.presentation.PlayerPresenter
import com.example.playlistmaker.search.api.ITunesRepository
import com.example.playlistmaker.search.models.Track

object Creator {

    private fun getITunesRepository(context: Context): ITunesRepository {
        return ITunesRepositoryImpl(
            RetrofitNetworkClient(context),
            LocalStorage(context.getSharedPreferences("local_storage", Context.MODE_PRIVATE)),
        )
    }

    fun provideITunesInteractor(context: Context): ITunesInteractor {
        return ITunesInteractorImpl(getITunesRepository(context))
    }

    fun providePlayerPresenter(
        posterView: PlayerView,
        track: Track
    ): PlayerPresenter {
        return PlayerPresenter(playerView, track)
    }
}