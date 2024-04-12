package com.example.playlistmaker.player.service

sealed class PlayerServiceState(val buttonState: Boolean, val isPlaying: Boolean, val progress: String) {

    class Default : PlayerServiceState(false, false, "00:00")

    class Prepared : PlayerServiceState(true, false, "00:00")

    class Playing(progress: String) : PlayerServiceState(true, true, progress)

    class Paused(progress: String) : PlayerServiceState(true, false, progress)
}