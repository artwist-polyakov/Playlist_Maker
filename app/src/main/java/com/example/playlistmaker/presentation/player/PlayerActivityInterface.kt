package com.example.playlistmaker.presentation.player

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.models.TrackInformation
import com.google.android.material.floatingactionbutton.FloatingActionButton

interface PlayerActivityInterface {
    fun showTrackInfo(trackInfo: TrackInformation)
    fun showPlayState()
    fun showPauseState()

    fun showPreparationState()

    fun showReadyState()

    fun setTime(time: String)

}