package com.example.playlistmaker.player.domain

import com.example.playlistmaker.common.presentation.models.TrackInformation

interface TrackStorageInteractor {
    fun giveMeLastTrack(): TrackInformation
}