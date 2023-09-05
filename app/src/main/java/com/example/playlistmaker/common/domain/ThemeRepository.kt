package com.example.playlistmaker.common.domain

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun saveTheme(isDark: Boolean)
    fun getPreferredThemeMode(): Int
}