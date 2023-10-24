package com.example.playlistmaker.media.data

import android.net.Uri
import com.example.playlistmaker.media.domain.ImagesRepository
import com.example.playlistmaker.media.domain.ImagesStorageInteractor

class ImagesStorageInteractorImpl(
    val repository: ImagesRepository
): ImagesStorageInteractor {
    override fun saveImage(uri: Uri) {
        repository.saveImage(uri)
    }

    override fun clearAllImages() {
        repository.clearAllImages()
    }
}