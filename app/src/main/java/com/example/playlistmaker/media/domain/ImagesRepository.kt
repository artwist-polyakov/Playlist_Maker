package com.example.playlistmaker.media.domain

import android.net.Uri

interface ImagesRepository {
    fun saveImage(uri: Uri): String
    fun clearAllImages()
}