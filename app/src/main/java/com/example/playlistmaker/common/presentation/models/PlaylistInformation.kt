package com.example.playlistmaker.common.presentation.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
data class PlaylistInformation(
    val id: UUID,
    val name: String,
    val description: String,
    val image: Uri?,
    val tracksCount: Int,
    val creationDate: Long
): Parcelable {}
