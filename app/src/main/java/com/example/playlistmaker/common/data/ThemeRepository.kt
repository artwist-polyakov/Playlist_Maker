package com.example.playlistmaker.common.data

import android.content.Context
import android.content.res.Configuration


class ThemeRepository(private val context: Context) {
    companion object {
        const val PREFS = "my_prefs"
        const val THEME_PREF = "isDarkTheme"
    }

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isDarkTheme(): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return prefs.getBoolean(THEME_PREF, currentNightMode == Configuration.UI_MODE_NIGHT_YES)
    }

    fun switchTheme(isDark: Boolean) {
        with(prefs.edit()) {
            putBoolean(THEME_PREF, isDark)
            apply()
        }
    }
}
