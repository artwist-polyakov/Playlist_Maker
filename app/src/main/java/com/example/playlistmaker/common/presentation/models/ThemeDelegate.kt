package com.example.playlistmaker.common.presentation.models

import com.example.playlistmaker.common.data.ThemeSettings

interface ThemeDelegate {

    fun updateTheme(theme: ThemeSettings)

}