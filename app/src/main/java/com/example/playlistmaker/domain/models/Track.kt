package com.example.playlistmaker.domain.models

import android.os.Parcelable
import com.example.playlistmaker.data.dto.TrackDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Long,
    val trackName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val artworkUrl512: String,
    val collectionName: String?,
    val relizeYear: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Parcelable {

    override fun hashCode(): Int {
        return this.trackId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TrackDto) return false
        return this.trackId == other.trackId
    }

}

