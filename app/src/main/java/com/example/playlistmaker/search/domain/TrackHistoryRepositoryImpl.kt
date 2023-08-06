package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.dto.LinkedRepository
import com.example.playlistmaker.search.models.Track
import com.example.playlistmaker.search.data.storage.TracksStorage

// TODO: Ввести этот класс вместо LinkedRepository для более человекопонятного названия класса и кода
class TrackHistoryRepositoryImpl(maxSize: Int) : LinkedRepository<Track>(maxSize),
    TracksStorage {

}