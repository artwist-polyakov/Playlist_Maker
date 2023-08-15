package com.example.playlistmaker.common.domain

import com.example.playlistmaker.common.data.ThemeSettings

interface ThemeInteractor {
    fun getTemeSettings(): ThemeSettings
}