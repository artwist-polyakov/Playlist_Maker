package com.example.playlistmaker.domain.models

import java.text.SimpleDateFormat
import java.util.Locale

class TrackDurationTime(val milliseconds: Int) {
    val mmss: String
        get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(milliseconds)

    override fun toString(): String {
        return mmss
    }
}