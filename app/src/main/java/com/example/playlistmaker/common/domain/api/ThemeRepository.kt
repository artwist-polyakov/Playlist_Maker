package com.example.playlistmaker.common.domain.api

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun saveTheme(isDark: Boolean)
}