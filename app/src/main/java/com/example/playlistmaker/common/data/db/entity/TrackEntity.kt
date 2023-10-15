package com.example.playlistmaker.common.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_table")
data class TrackEntity(
    @PrimaryKey
    val id: Long,
    val lastLikeUpdate: Long,
    val isLiked: Boolean,
    val trackName: String,
    val trackTime: String,
    val artistName: String,
    val artworkUrl100: String,
    val artworkUrl512: String,
    val collectionName: String?,
    val relizeYear: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
)
