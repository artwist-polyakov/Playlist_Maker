package com.example.playlistmaker.presentation.player

import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.ui.player.PlayerActivity

class PlayerPresenter(
    override var view: PlayerInterface?,
    private val playInteractor: PlayInteractor = PlayInteractor()
) : PlayerPresenterInterface {

    private var currentButtonState = READY_TO_PLAY

    override fun play() {
        playInteractor.play()
    }

    override fun pause() {
        playInteractor.play()
    }

    override fun bindScreen() {
        if (view != null) {
            val track = view!!.currentTrack
            // Страна не пустая?
            if (track!!.country == null) {
                view!!.trackCountryInfoGroup!!.visibility = Group.GONE
            } else {
                view!!.trackCountryInfoGroup!!.visibility = Group.VISIBLE
                view!!.trackCountry!!.text = track!!.country
            }

            // Трек не пустой?
            if (view!!.currentTrack!!.trackName == "") {
                view!!.trackInfoGroup!!.visibility = Group.GONE
            } else {
                view!!.trackInfoGroup!!.visibility = Group.VISIBLE
                view!!.trackName!!.text = view!!.currentTrack!!.trackName
                view!!.artistName!!.text = view!!.currentTrack!!.artistName
                view!!.trackDuration!!.text = view!!.currentTrack!!.trackTime
                view!!.trackAlbumName!!.text = view!!.currentTrack!!.collectionName
                view!!.trackReleaseYear!!.text = view!!.currentTrack!!.relizeYear
                view!!.trackGenre!!.text = view!!.currentTrack!!.primaryGenreName
                Glide.with(view as PlayerActivity)
                    .load(view!!.currentTrack!!.artworkUrl512)
                    .into(view!!.trackCover!!)
            }

        }
    }

    override fun setTime() {
        TODO("Not yet implemented")
    }

    override fun changePlayButton() {
        if (view != null) {
            if (currentButtonState == 0) {
                view!!.playButton!!.setImageResource(R.drawable.pause_button)
                currentButtonState = READY_TO_PAUSE
            } else {
                view!!.playButton!!.setImageResource(R.drawable.play_button)
                currentButtonState = READY_TO_PLAY
            }
        }
    }

    override fun resetPlayer() {
        if (currentButtonState == 1) {
            changePlayButton()
        }


    }

    companion object {
        private const val READY_TO_PLAY = 0
        private const val READY_TO_PAUSE = 1
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