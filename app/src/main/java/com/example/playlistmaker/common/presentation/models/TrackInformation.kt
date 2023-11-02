package com.example.playlistmaker.common.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackInformation(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val artworkUrl512: String,
    val collectionName: String?,
    val relizeYear: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Parcelable {

    fun countDurationInSeconds(): Int {
        var trackTimeComponents: Map<Int, Int> = mapOf(
            0 to 1,
            1 to 60,
            2 to 3600,
            3 to 3600*24
        )
        var result: Int = 0
        val trackTimeComponentsList: List<Int> = trackTime.split(":").map { it.toInt() }
        for (i in trackTimeComponentsList.indices) {
            val timeComponent = trackTimeComponentsList[trackTimeComponentsList.size - i - 1]
            val multiplier = trackTimeComponents[i] ?: 0
            result += timeComponent * multiplier
        }
        return result
    }

}

