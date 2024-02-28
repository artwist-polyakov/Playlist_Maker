package com.example.playlistmaker.player.domain

interface MediaPlayerCallbackInterface {
    fun onMediaPlayerReady()
    fun onMediaPlayerPause()
    fun onMediaPlayerPlay()
}
