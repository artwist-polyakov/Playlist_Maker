package com.example.playlistmaker.history

import com.example.playlistmaker.model.Track

interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}