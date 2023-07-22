package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.data.dto.TrackDto

object PresenterCreator {
    var playerPresenter: PlayerPresenterInterface? = null

    fun giveMeMyPresenter(view: PlayerActivityInterface, track: TrackDto): PlayerPresenterInterface {
        if (playerPresenter == null) {
            playerPresenter = PlayerPresenter(view, track)
        } else {
            playerPresenter!!.changeView(view)
            if (playerPresenter!!.track != track) {
                playerPresenter!!.changeTrack(track)
            }
        }
        return playerPresenter!!
    }
}