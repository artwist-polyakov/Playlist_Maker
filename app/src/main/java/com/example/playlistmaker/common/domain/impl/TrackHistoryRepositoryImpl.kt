package com.example.playlistmaker.common.domain.impl

import com.example.playlistmaker.common.data.dto.LinkedRepository
import com.example.playlistmaker.common.domain.models.Track
import com.example.playlistmaker.common.domain.models.TracksRepository

// TODO: Ввести этот класс вместо LinkedRepository для более человекопонятного названия класса и кода
class TrackHistoryRepositoryImpl(maxSize: Int) : LinkedRepository<Track>(maxSize),
    TracksRepository {

}