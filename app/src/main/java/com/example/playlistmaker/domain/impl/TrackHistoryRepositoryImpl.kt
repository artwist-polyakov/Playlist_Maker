package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.dto.LinkedRepository
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.TracksRepository

class TrackHistoryRepositoryImpl (maxSize: Int): LinkedRepository<TrackDto>(maxSize), TracksRepository {

}