package com.example.playlistmaker.common.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlistmaker.common.data.db.dao.PlaylistDao
import com.example.playlistmaker.common.data.db.dao.TrackDao
import com.example.playlistmaker.common.data.db.entity.PlaylistEntity
import com.example.playlistmaker.common.data.db.entity.PlaylistTrackReference
import com.example.playlistmaker.common.data.db.entity.TrackEntity

@Database(
    version = 12,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackReference::class
    ]
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
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


        val MIGRATION_11_12: Migration = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS `new_playlists` (
                `id` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `description` TEXT NOT NULL, 
                `imageUri` TEXT, 
                `tracksCount` INTEGER NOT NULL, 
                `creationDate` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)

                database.execSQL("""
            INSERT INTO `new_playlists` (`id`, `name`, `description`, `imageUri`, `tracksCount`, `creationDate`)
            SELECT `id`, `name`, `description`, `imageUri`, `tracksCount`, `creationDate` FROM `playlists`
        """)

                database.execSQL("DROP TABLE `playlists`")

                database.execSQL("ALTER TABLE `new_playlists` RENAME TO `playlists`")
            }
        }
    }
}