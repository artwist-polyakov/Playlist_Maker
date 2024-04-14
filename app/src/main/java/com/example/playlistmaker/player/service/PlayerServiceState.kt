package com.example.playlistmaker.player.service

sealed class PlayerServiceState(val buttonState: Boolean, val isPlaying: Boolean, val progress: Int) {

    class Default : PlayerServiceState(false, false, 0)

    class Prepared : PlayerServiceState(true, false, 0)

    class Playing(progress: Int) : PlayerServiceState(true, true, progress)

    class Paused(progress: Int) : PlayerServiceState(true, false, progress)
}