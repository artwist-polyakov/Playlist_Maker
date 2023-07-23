package com.example.playlistmaker.presentation.player

import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.api.MediaPlayerInterface
import com.example.playlistmaker.data.repository.MediaPlayerImpl
import com.example.playlistmaker.domain.models.TrackDurationTime
import com.example.playlistmaker.domain.usecases.PlayButtonInteractUseCase
import com.example.playlistmaker.ui.player.PlayerActivity


class PlayerPresenter(
    override var view: PlayerActivityInterface?,
    override var track: Track
) : PlayerPresenterInterface, MediaPlayerCallback {
    private var currentButtonState = READY_TO_PLAY
    private lateinit var playButtonUseCase: PlayButtonInteractUseCase
    private var mediaPlayer: MediaPlayerInterface? = null
    override fun bindScreen() {
        view?.let {
            // Страна не пустая?
            if (track.country == null) {
                it.trackCountryInfoGroup?.visibility = Group.GONE
            } else {
                it.trackCountryInfoGroup?.visibility = Group.VISIBLE
                it.trackCountry?.text = track.country
            }

            // Трек не пустой?
            if (track.trackName == "") {
                it.playButton?.isEnabled = false
                it.trackInfoGroup?.visibility = Group.GONE
            } else {
                if (mediaPlayer == null) {
                    it.playButton?.isEnabled = false
                    // Создаем новый MediaPlayer, если он еще не был создан
                    mediaPlayer = MediaPlayerImpl(this, withTrack = track)
                }
                it.trackInfoGroup?.visibility = Group.VISIBLE
                it.trackName?.text = track.trackName
                it.artistName?.text = track.artistName
                it.trackDuration?.text = track.trackTime
                it.trackAlbumName?.text = track.collectionName
                it.trackReleaseYear?.text = track.relizeYear
                it.trackGenre?.text = track.primaryGenreName
                (it as? PlayerActivity)?.let{unwrapView ->
                    Glide.with(unwrapView)
                        .load(track.artworkUrl512)
                        .into(unwrapView.trackCover!!)
                }
                it.playButton?.setOnClickListener{
                    playButtonUseCase = PlayButtonInteractUseCase(player = this.mediaPlayer)
                    playButtonUseCase.execute()
                }
            }
        }
    }

    override fun changePlayButton() {
        view?.let{
            if (currentButtonState == READY_TO_PLAY) {
                it.playButton?.setImageResource(R.drawable.pause_button)
                currentButtonState = READY_TO_PAUSE
            } else {
                it.playButton?.setImageResource(R.drawable.play_button)
                currentButtonState = READY_TO_PLAY
            }
        }
    }

    override fun pausePresenter() {
        if (currentButtonState == READY_TO_PAUSE) {
            changePlayButton()
            mediaPlayer?.pausePlayer()
        }
    }

    override fun changeView(newView: PlayerActivityInterface) {
        this.view = newView
    }

    private fun setPlayIcon() {
        view?.let {
            it.playButton?.setImageResource(R.drawable.play_button)
        }
    }

    override fun changeTrack(track: Track) {
        view?.let {
            view?.playButton?.isEnabled = false
        }
        this.track = track
        mediaPlayer?.changeTrack(track)
        currentButtonState = READY_TO_PLAY
        setPlayIcon()
    }
    override fun resetPlayer() {
        if (currentButtonState == 1) {
            changePlayButton()
            mediaPlayer?.destroyPlayer()
            mediaPlayer = null
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
            it.playButton?.isEnabled = true
        }
    }

    override fun onMediaPlayerTimeUpdate(time: TrackDurationTime) {
        view?.let {
            it.trackTime?.text = time.toString()
        }
    }
}
