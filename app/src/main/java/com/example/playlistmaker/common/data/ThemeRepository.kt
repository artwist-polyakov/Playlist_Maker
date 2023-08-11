package com.example.playlistmaker.common.data

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun saveTheme(isDark: Boolean)
}