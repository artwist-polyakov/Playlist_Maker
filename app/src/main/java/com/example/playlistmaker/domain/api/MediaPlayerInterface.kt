package com.example.playlistmaker.domain.api

import javax.security.auth.callback.Callback

interface MediaPlayerInterface {

    fun playPauseSwitcher()

    fun destroyPlayer()

    fun updateProgress(callback: Callback)



}