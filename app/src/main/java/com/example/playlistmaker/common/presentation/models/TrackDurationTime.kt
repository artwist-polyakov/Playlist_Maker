package com.example.playlistmaker.common.presentation.models

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

class TrackDurationTime(val milliseconds: Int) {
    override fun toString(): String {
        val result = SimpleDateFormat("mm:ss", Locale.getDefault()).format(milliseconds)
        if (result == "00:00") {
            Log.d("TrackDurationTime", "result is 00:00 with input $milliseconds")
        }
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(milliseconds)
    }
}