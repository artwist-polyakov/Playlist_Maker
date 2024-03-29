package com.example.playlistmaker.common.di

import androidx.room.Room
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.media.data.ImagesRepositoryImpl
import com.example.playlistmaker.media.domain.ImagesRepository
import com.example.playlistmaker.settings.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::ExternalNavigatorImpl) bind ExternalNavigator::class

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .addMigrations(
                AppDatabase.MIGRATION_10_11,
                AppDatabase.MIGRATION_11_12,
                AppDatabase.MIGRATION_12_13,
                AppDatabase.MIGRATION_13_14
            )
            .build()
    } bind AppDatabase::class

    single<ImagesRepository> { ImagesRepositoryImpl(get(), "playlist_images") }
}