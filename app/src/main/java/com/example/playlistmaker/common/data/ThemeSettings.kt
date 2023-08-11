package com.example.playlistmaker.common.data

sealed class ThemeSettings {
    object Light : ThemeSettings()
    object Dark : ThemeSettings()
}