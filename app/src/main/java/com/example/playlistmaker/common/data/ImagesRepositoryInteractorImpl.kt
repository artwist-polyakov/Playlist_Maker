package com.example.playlistmaker.common.data

import android.net.Uri
import com.example.playlistmaker.media.domain.ImagesRepository
import com.example.playlistmaker.media.domain.ImagesRepositoryInteractor

class ImagesRepositoryInteractorImpl(
    val repository: ImagesRepository
) : ImagesRepositoryInteractor {
    override fun saveImage(uri: Uri): String {
        return repository.saveImage(uri)
    }

    override fun clearAllImages() {
        repository.clearAllImages()
    }

    override fun removeImage(uri: Uri): Boolean {
        return repository.removeImage(uri)
    }
}