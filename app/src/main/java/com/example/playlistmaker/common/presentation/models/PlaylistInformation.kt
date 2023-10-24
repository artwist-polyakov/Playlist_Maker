package com.example.playlistmaker.common.presentation.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaylistInformation(
    val id: Long,
    val name: String,
    val description: String,
    val image: Uri?,
    val tracksCount: Int,
    val creationDate: Long
): Parcelable {}
