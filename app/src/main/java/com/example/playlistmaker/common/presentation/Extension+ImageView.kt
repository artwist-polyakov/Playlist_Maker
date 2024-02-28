package com.example.playlistmaker.common.presentation

import android.net.Uri
import android.widget.ImageView

fun ImageView.setImageUriOrDefault(uri: Uri?, default: Int) {
    if (uri != null) {
        this.setImageURI(uri)
        // Проверка, что изображение действительно установлено, если drawable не установлен, используем плейсхолдер
        if (this.drawable == null) {
            this.setImageResource(default)
        }
    } else {
        this.setImageResource(default)
    }
}
