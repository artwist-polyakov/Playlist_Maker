package com.example.playlistmaker.common.presentation

import com.example.playlistmaker.common.data.ThemeSettings

interface ThemeDelegate {

    fun updateTheme(theme: ThemeSettings)

}