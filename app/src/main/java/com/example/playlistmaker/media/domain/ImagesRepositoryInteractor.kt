package com.example.playlistmaker.media.domain

import android.net.Uri

interface ImagesRepositoryInteractor {
    fun saveImage(uri: Uri): String
    fun clearAllImages()
}