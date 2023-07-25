package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.presentation.models.TrackInformation

object PresenterCreator {
    var playerPresenter: PlayerPresenterInterface? = null

    fun giveMeMyPresenter(
        view: PlayerActivityInterface,
        track: TrackInformation,
        presenterFactory: (PlayerActivityInterface, TrackInformation) -> PlayerPresenterInterface
    ): PlayerPresenterInterface {
        playerPresenter?.let{
            if (it.view != null) {
                it.detachView()
            }
            it.attachView(view)
            if (it.track != track) {
                it.changeTrack(track)
            }
        } ?: run {
            playerPresenter = presenterFactory(view, track)
        }
        playerPresenter!!.initPlayer()
        return playerPresenter!!
    }
}