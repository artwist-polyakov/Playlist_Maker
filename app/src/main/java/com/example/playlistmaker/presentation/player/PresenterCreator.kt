package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.presentation.models.TrackInformation

object PresenterCreator {
    var playerPresenter: PlayerPresenterInterface? = null

    fun giveMeMyPresenter(
        view: PlayerActivityInterface,
        track: TrackInformation,
        presenterFactory: (PlayerActivityInterface, TrackInformation) -> PlayerPresenterInterface
    ): PlayerPresenterInterface {
        if (playerPresenter == null) {
            playerPresenter = presenterFactory(view, track)
        } else {
            if (playerPresenter!!.view != null) {
                playerPresenter!!.detachView()
            }
            playerPresenter!!.attachView(view)
            if (playerPresenter!!.track != track) {
                playerPresenter!!.changeTrack(track)
            }
        }
        playerPresenter!!.initPlayer()
        return playerPresenter!!
    }
}