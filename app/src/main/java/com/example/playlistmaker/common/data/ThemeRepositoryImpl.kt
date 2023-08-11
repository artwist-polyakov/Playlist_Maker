package com.example.playlistmaker.common.data

import android.content.Context
import android.content.res.Configuration
import android.util.Log


class ThemeRepositoryImpl(private val context: Context): ThemeRepository {
    companion object {
        const val PREFS = "my_prefs"
        const val THEME_PREF = "isDarkTheme"
    }

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    override fun isDarkTheme(): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return prefs.getBoolean(THEME_PREF, currentNightMode == Configuration.UI_MODE_NIGHT_YES)
    }

    override fun saveTheme(isDark: Boolean) {
        with(prefs.edit()) {
            putBoolean(THEME_PREF, isDark)
            apply()
        }
        Log.d("ThemeRepository", "Theme switched to: ${if (isDark) "Dark" else "Light"}")
    }

    fun switchTheme(isDark: Boolean) {
        with(prefs.edit()) {
            putBoolean(THEME_PREF, isDark)
            apply()
        }
        Log.d("ThemeRepository", "Theme switched to: ${if (isDark) "Dark" else "Light"}")
    }
}
