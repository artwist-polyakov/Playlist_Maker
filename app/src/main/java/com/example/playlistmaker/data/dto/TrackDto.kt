package com.example.playlistmaker.data.dto

import android.os.Parcelable
import com.example.playlistmaker.domain.models.Track
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class TrackDto(val trackId: Long,
                    val trackName: String,
                    val artistName: String,
                    val trackTime: String,
                    val artworkUrl100: String,
                    val artworkUrl512: String,
                    val collectionName: String?,
                    val relizeYear: String?,
                    val primaryGenreName: String?,
                    val country: String?,
                    val previewUrl: String?
                    ) : Parcelable

{
    constructor(from: Track) : this(
        trackId = from.trackId,
        trackName = from.trackName,
        artistName = from.artistName,
        trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(from.trackTimeMillis),
        artworkUrl100 = from.artworkUrl100,
        artworkUrl512 = from.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
        collectionName = from.collectionName,
        relizeYear = SimpleDateFormat("yyyy", Locale.getDefault())
            .format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                .parse(from.releaseDate)),
        primaryGenreName = from.primaryGenreName,
        country = from.country,
        previewUrl = from.previewUrl)

    override fun hashCode(): Int {
        return this.trackId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Track) return false
        return this.trackId == other.trackId
    }

}

