package com.example.playlistmaker.common.data

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.example.playlistmaker.common.domain.ThemeRepository

class ThemeRepositoryImpl(private val context: Context): ThemeRepository {
    companion object {
        const val PREFS = "my_prefs"
        const val THEME_PREF = "isDarkTheme"
    }

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    override fun isDarkTheme(): Boolean {
        val defaultNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val currentNightMode = when (defaultNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
        return prefs.getBoolean(THEME_PREF, currentNightMode)
    }

    override fun saveTheme(isDark: Boolean) {
        with(prefs.edit()) {
            putBoolean(THEME_PREF, isDark)
            apply()
        }
    }
}
