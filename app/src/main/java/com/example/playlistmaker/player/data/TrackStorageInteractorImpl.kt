package com.example.playlistmaker.player.data

import com.example.playlistmaker.common.presentation.mappers.TrackDtoToTrackMapper
import com.example.playlistmaker.common.presentation.mappers.TrackToTrackInformationMapper
import com.example.playlistmaker.common.presentation.models.TrackInformation
import com.example.playlistmaker.player.domain.TrackStorageInteractor
import com.example.playlistmaker.search.data.storage.TracksStorage

class TrackStorageInteractorImpl(private val tracksStorage: TracksStorage) : TrackStorageInteractor {
    override fun giveMeLastTrack(): TrackInformation {
        val initializedTrack = TrackDtoToTrackMapper().invoke( tracksStorage.takeHistory(reverse = true).first())
        return TrackToTrackInformationMapper().invoke(initializedTrack)
    }
}