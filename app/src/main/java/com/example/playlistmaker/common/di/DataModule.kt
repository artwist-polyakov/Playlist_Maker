package com.example.playlistmaker.common.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.media.data.ImagesRepositoryImpl
import com.example.playlistmaker.media.domain.ImagesRepository
import com.example.playlistmaker.settings.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val MIGRATION_10_11: Migration = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL("""
        CREATE TABLE IF NOT EXISTS `playlists` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
            `name` TEXT NOT NULL, 
            `description` TEXT NOT NULL, 
            `imageUri` TEXT, 
            `tracksCount` INTEGER NOT NULL, 
            `creationDate` INTEGER NOT NULL
        )
    """)

        database.execSQL("""
        CREATE TABLE IF NOT EXISTS `playlist_track_reference` (
            `playlistId` INTEGER NOT NULL, 
            `trackId` INTEGER NOT NULL, 
            `lastUpdate` INTEGER NOT NULL, 
            PRIMARY KEY(`playlistId`, `trackId`), 
            FOREIGN KEY(`playlistId`) REFERENCES `playlists`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, 
            FOREIGN KEY(`trackId`) REFERENCES `music_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
        )
    """)
    }
}

val dataModule = module {
    single<ExternalNavigator> { ExternalNavigatorImpl( get() ) }
    single {
        Room.databaseBuilder( androidContext(), AppDatabase::class.java, "database.db" )
            .addMigrations( MIGRATION_10_11 )
            .build()
    }
    single<ImagesRepository> { ImagesRepositoryImpl( get(), "playlist_images" )}
}