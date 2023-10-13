package com.example.playlistmaker.player.ui.view_model

sealed class PlayerState {
    object Loading : PlayerState()
    object Ready : PlayerState()
    object Play : PlayerState()
    object Pause : PlayerState()

}