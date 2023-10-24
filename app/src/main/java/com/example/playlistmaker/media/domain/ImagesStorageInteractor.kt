package com.example.playlistmaker.media.domain

import android.net.Uri

interface ImagesStorageInteractor {
    fun saveImage(uri: Uri)
    fun clearAllImages()
}