package com.example.playlistmaker.common.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val imageUri: String?,
    val tracksCount: Int,
    val playlistDurationSeconds: Long,
    val wasDurationCalculated: Boolean,
    val creationDate: Long
)
