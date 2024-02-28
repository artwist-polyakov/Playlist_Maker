package com.example.playlistmaker.common.domain

interface ThemeInteractor {
    fun getTemeSettings(): ThemeSettings
    fun getPreferredThemeMode(): Int
}
