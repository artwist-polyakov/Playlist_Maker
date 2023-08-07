package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.api.Resource
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.storage.TracksStorage
import com.example.playlistmaker.search.domain.api.TracksRepository

class TracksRepositoryImpl (
    private val networkClient: NetworkClient,
    // 1
    private val localStorage: TracksStorage,
) : TracksRepository {

    override fun searchTracks(expression: String): Resource<List<TrackDto>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                val history = localStorage.takeHistory(true)

                Resource.Success((response as TracksSearchResponse).results.map {
                    TrackDto(

//                        val trackId: Long,
//                    val trackName: String,
//                    val artistName: String,
//                    val trackTimeMillis: Int,
//                    val artworkUrl100: String,
//                    val collectionName: String?,
//                    val releaseDate: String?,
//                    val primaryGenreName: String?,
//                    val country: String?,
//                    val previewUrl: String?
//
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        trackTimeMillis = it.trackTimeMillis,
                        artworkUrl100 = it.artworkUrl100,
                        collectionName = it.collectionName,
                        releaseDate = it.releaseDate,
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl,
                    )
                })
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun addTrackToHistory(track: TrackDto) {
        localStorage.pushTrackToHistory(track)
    }
}