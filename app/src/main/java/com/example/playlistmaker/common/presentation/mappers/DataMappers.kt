package com.example.playlistmaker.common.presentation.mappers

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

//class TrackDtoToTrackMapper : (TrackDto) -> Track {
//    /* TODO Этот класс будет превращать сырые данные из интернета в данные для сёрч активити и обратно.
//    кажется, что у нас не должно быть строковых измеримых и сравнимых свойств, например
//    если мы решим сортировать по дате выхода наши треки в результатах поиска, то для сортировки нужна
//    числовая, а не строковая дата выхода.
//    */
//    override fun invoke(dto: TrackDto): Track {
//        return Track()
//    }
//}
//
//class TrackToTrackDtoMapper : (Track) -> TrackDto {
//    override fun invoke(track: Track): TrackDto {
//        return TrackDto()
//    }
//}

class TrackDtoToTrackInformationMapper : (TrackDto) -> TrackInformation {
    override fun invoke(dto: TrackDto): TrackInformation {
        val result = TrackInformation(
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
                        .parse(dto.releaseDate)
                ),
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
        return result
    }
}

class TrackDtoToTrackMappers : (TrackDto) -> Track {
    override fun invoke(dto: TrackDto): Track {
        val result = Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            trackTime =  SimpleDateFormat("mm:ss", Locale.getDefault()).format(dto.trackTimeMillis),
            artistName = dto.artistName,
            artworkUrl100 = dto.artworkUrl100,
            artworkUrl512 = dto.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
            collectionName = dto.collectionName,
            relizeYear = dto.releaseDate?.let {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    .parse(it)?.let {
                        SimpleDateFormat("yyyy", Locale.getDefault())
                            .format(
                                it
                            )
                    }
            },
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
        return result
    }
}

class TrackToTrackInformationMappers : (Track) -> TrackInformation {
    override fun invoke(track: Track): TrackInformation {
        val result = TrackInformation(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            artworkUrl512 = track.artworkUrl512,
            collectionName = track.collectionName,
            relizeYear = track.relizeYear,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
        return result
    }
}

class TrackToTrackDtoMapper : (Track) -> TrackDto {
    override fun invoke(track: Track): TrackDto {
        val result = TrackDto(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTime.let {
                SimpleDateFormat("mm:ss", Locale.getDefault()).parse(it).time
            }.toInt(),
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.relizeYear?.let {
                try {
                    val sdfInput = SimpleDateFormat("yyyy", Locale.getDefault())
                    val date = sdfInput.parse(it)
                    val sdfOutput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    sdfOutput.format(date)
                } catch (e: Exception) {
                    null
                }
            },
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
        return result
    }
}