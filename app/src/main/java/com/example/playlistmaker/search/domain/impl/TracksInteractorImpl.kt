package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.common.presentation.models.TrackDtoToTrackMapper
import com.example.playlistmaker.search.data.network.Resource
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when(val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data?.map { dto ->
                        TrackDtoToTrackMapper().invoke(dto)}
                        , null)
                }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
        }
    }

    override fun addTrackToHistory(track: TrackDto) {
        repository.addTrackToHistory(track)
    }
}
