package com.example.playlistmaker.common.domain

sealed class ThemeSettings {
    object Light : ThemeSettings()
    object Dark : ThemeSettings()
}
