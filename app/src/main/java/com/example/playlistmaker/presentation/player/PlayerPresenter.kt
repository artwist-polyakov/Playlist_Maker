package com.example.playlistmaker.presentation.player

class PlayerPresenter(override var view: PlayerInterface?) : PlayerPresenterInterface {
    constructor() : this(null)
}