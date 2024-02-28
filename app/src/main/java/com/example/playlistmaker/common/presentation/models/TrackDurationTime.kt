package com.example.playlistmaker.common.presentation.models

import java.text.SimpleDateFormat
import java.util.Locale

class TrackDurationTime(val milliseconds: Int) {
    override fun toString(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(milliseconds)
    }
}
