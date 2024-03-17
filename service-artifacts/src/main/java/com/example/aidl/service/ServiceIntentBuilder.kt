package com.example.aidl.service

import android.content.Intent

fun buildAudioPlayerBindIntent(): Intent {
    // сервис в основном приложении ловит через интент фильтр action который мы указали здесь,
    // смотри манифест основного приложения
    return Intent("com.example.playlistmaker.service.AudioPlayerService.BIND")
        .setPackage("com.example.playlistmaker")
}
