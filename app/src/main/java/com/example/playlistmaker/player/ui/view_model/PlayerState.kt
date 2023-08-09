package com.example.playlistmaker.player.ui.view_model

import com.example.playlistmaker.common.presentation.models.TrackDurationTime

sealed class PlayerState {
    object Loading : PlayerState()
    object Ready : PlayerState()
    object Play : PlayerState()
    object Pause : PlayerState()
    data class TimeUpdate(val time: TrackDurationTime) : PlayerState()

}