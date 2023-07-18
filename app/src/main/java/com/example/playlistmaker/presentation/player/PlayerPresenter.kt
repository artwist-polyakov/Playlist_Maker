package com.example.playlistmaker.presentation.player

class PlayerPresenter(override var view: PlayerInterface?,
private val playInteractor: PlayInteractor = PlayInteractor()) : PlayerPresenterInterface {
    override fun play() {
        playInteractor.play()
    }

    override fun pause() {
        playInteractor.play()
    }

}

class PlayUseCase {
    fun execute() {}
}

class PauseUseCase {
    fun execute() {}
}

class PlayInteractor {
    fun play() {}
    fun pause() {}
}


class musicRepository {

}