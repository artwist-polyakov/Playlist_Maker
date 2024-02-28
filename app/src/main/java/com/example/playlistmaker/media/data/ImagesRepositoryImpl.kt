package com.example.playlistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.playlistmaker.media.domain.ImagesRepository
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class ImagesRepositoryImpl(
    private val context: Context,
    private val album: String
) : ImagesRepository {

    override fun saveImage(uri: Uri): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), album)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val uuid = UUID.randomUUID().toString()
        val file = File(filePath, "$uuid.jpg")
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
                        ?: throw IOException("Не удалось декодировать изображение")
                }
            } ?: throw IOException("Не удалось открыть поток ввода")
        } catch (e: Exception) {
            e.printStackTrace()
            throw IOException("Не удалось сохранить изображение")
        }
        return file.absolutePath
    }

    override fun clearAllImages() {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), album)
        if (!filePath.exists()) {
            Log.d("ImagesRepositoryImpl", "Папка не существует")
            return
        }
        filePath.listFiles()?.forEach {
            Log.d("ImagesRepositoryImpl", "Удаляем файл ${it.absolutePath}")
        }
        filePath.deleteRecursively()
    }

    override fun removeImage(uri: Uri): Boolean {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), album)
        val file = File(filePath, uri.lastPathSegment ?: "")
        return try {
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        const val QUALITY = 80
    }
}
