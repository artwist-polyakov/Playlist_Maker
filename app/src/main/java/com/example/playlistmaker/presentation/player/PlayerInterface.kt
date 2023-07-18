package com.example.playlistmaker.presentation.player

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.floatingactionbutton.FloatingActionButton

interface PlayerInterface {

    // FiELDS
    var trackCountryInfoGroup: Group?
    var trackCountry: TextView?
    var playerPresenter: PlayerPresenterInterface?
    var currentTrack: TrackDto?
    var trackInfoGroup: Group?
    var trackName: TextView?
    var artistName: TextView?
    var trackDuration: TextView?
    var trackAlbumName: TextView?
    var trackReleaseYear: TextView?
    var trackGenre: TextView?
    var trackCover: ImageView?

    // IMTERFACE
    var playButton: FloatingActionButton?
    fun play()

    fun pause()
}