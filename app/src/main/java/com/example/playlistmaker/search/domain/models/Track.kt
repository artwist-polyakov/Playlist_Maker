package com.example.playlistmaker.search.domain.models

import android.os.Parcelable
import com.example.playlistmaker.search.data.dto.TrackDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Long,
    val trackName: String,
    val trackTime: String,
    val artistName: String,
    val artworkUrl100: String,
    val artworkUrl512: String,
    val collectionName: String?,
    val relizeYear: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : Parcelable {

    override fun hashCode(): Int {
        return this.trackId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TrackDto) return false
        return this.trackId == other.trackId
    }

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