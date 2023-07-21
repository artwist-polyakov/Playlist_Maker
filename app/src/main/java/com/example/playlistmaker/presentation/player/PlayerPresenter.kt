package com.example.playlistmaker.presentation.player

import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.MediaPlayerInterface
import com.example.playlistmaker.domain.impl.NextMediaPlayer
import com.example.playlistmaker.domain.usecases.PlayButtonInteractUseCase
import com.example.playlistmaker.ui.player.PlayerActivity


class PlayerPresenter(
    override var view: PlayerActivityInterface?,
) : PlayerPresenterInterface, MediaPlayerCallback {
    private var currentButtonState = READY_TO_PLAY
    private lateinit var playButtonUseCase: PlayButtonInteractUseCase
    private lateinit var mediaPlayer: MediaPlayerInterface
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
            it.playButton?.isEnabled = false
            // Трек не пустой?
            if (it.currentTrack?.trackName == "") {
                it.trackInfoGroup?.visibility = Group.GONE
            } else {
                Thread {
                    mediaPlayer = NextMediaPlayer(this, withTrack = track)
                }.start()
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

    override fun setProgressTime() {
        TODO("Not yet implemented")
    }

    override fun changePlayButton() {
        view?.let{
            if (currentButtonState == 0) {
                it.playButton?.setImageResource(R.drawable.pause_button)
                mediaPlayer.startPlayer()
                currentButtonState = READY_TO_PAUSE
            } else {
                it.playButton?.setImageResource(R.drawable.play_button)
                mediaPlayer.pausePlayer()
                currentButtonState = READY_TO_PLAY
            }
        }
    }

    override fun pausePresenter() {
        if (currentButtonState == 1) {
            changePlayButton()
            mediaPlayer.pausePlayer()
        }
    }

    override fun resetPlayer() {
        if (currentButtonState == 1) {
            changePlayButton()
        }
        view?.let {
            it.trackTime?.text = START_TIME
        }
    }

    companion object {
        private const val READY_TO_PLAY = 0
        private const val READY_TO_PAUSE = 1
        private const val START_TIME = "00:00"
    }

    override fun onMediaPlayerReady() {
        view?.let {
            playButtonUseCase = PlayButtonInteractUseCase(mediaPlayer)
            it.playButton?.isEnabled = true
            view?.let {
                it.playButton?.setOnClickListener{
                    playButtonUseCase.execute()
                }
            }
        }
    }

    override fun onMediaPlayerTimeUpdate(time: Int) {
        view?.let {
            it.trackTime?.text = time.toString()
        }
    }
}
