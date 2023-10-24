package com.example.playlistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.media.domain.ImagesRepository
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ImagesRepositoryImpl (
    private val context: Context
): ImagesRepository {

    override fun saveImage(uri: Uri, album: String): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), album)
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        val uuid = UUID.randomUUID().toString()
        val file = File(filePath, uuid)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.absolutePath
    }

    override fun clearAllImages(album: String) {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), album)
        if (!filePath.exists()){
            return
        }
        filePath.deleteRecursively()
    }
}