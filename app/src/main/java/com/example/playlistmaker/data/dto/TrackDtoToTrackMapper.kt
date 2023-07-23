package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackDtoToTrackMapper : (TrackDto) -> Track {
    override fun invoke(dto: TrackDto): Track {
        val result = Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(dto.trackTimeMillis),
            artworkUrl100 = dto.artworkUrl100,
            artworkUrl512 = dto.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
            collectionName = dto.collectionName,
            relizeYear = SimpleDateFormat("yyyy", Locale.getDefault())
                .format(
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    .parse(dto.releaseDate)),
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl)
        return result
    }
}