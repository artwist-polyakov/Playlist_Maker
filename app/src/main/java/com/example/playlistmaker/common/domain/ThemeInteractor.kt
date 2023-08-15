package com.example.playlistmaker.common.domain

import android.content.Context
import com.example.playlistmaker.common.data.ThemeSettings

interface ThemeInteractor {
    fun getTemeSettings(context: Context): ThemeSettings
}