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
        view?.let {
            val track = it.currentTrack
            // Страна не пустая?
            if (track?.country == null) {
                it.trackCountryInfoGroup?.visibility = Group.GONE
            } else {
                it.trackCountryInfoGroup?.visibility = Group.VISIBLE
                it.trackCountry?.text = track?.country
            }

            // Трек не пустой?
            if (it.currentTrack?.trackName == "") {
                it.trackInfoGroup?.visibility = Group.GONE
            } else {
                it.trackInfoGroup?.visibility = Group.VISIBLE
                it.trackName?.text = it.currentTrack?.trackName
                it.artistName?.text = it.currentTrack?.artistName
                it.trackDuration?.text = it.currentTrack?.trackTime
                it.trackAlbumName?.text = it.currentTrack?.collectionName
                it.trackReleaseYear?.text = it.currentTrack?.relizeYear
                it.trackGenre?.text = it.currentTrack?.primaryGenreName
                (it as? PlayerActivity)?.let{unwrapView ->
                    Glide.with(unwrapView)
                        .load(unwrapView.currentTrack!!.artworkUrl512)
                        .into(unwrapView.trackCover!!)
                }
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