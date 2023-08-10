package com.example.playlistmaker.creator.search

import android.content.Context
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.search.data.storage.TracksStorageImpl
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl

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

}