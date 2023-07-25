package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.dto.LinkedRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.TracksRepository

// TODO: Ввести этот класс вместо LinkedRepository для более человекопонятного названия класса и кода
class TrackHistoryRepositoryImpl(maxSize: Int) : LinkedRepository<Track>(maxSize),
    TracksRepository {

}