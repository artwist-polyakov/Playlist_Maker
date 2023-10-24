package com.example.playlistmaker.media.domain

import android.net.Uri

interface ImagesRepository {
    fun saveImage(uri: Uri, album: String): String
    fun clearAllImages(album: String)
}