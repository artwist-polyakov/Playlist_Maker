package com.example.playlistmaker.common.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val DarkAppColor = Color(0xFF1A1B22)
val GreyColor = Color(0xFFE6E8EB)
val TextColor = Color(0xFF1A1B22)
val MainScreenBackgroundColor = Color(0xFF3772E7)
val IconTintColor = Color(0xFFAEAFB4)
val ColorOnSecondaryDay = Color(0xFFAEAFB4)
val ColorOnSecondaryNight = Color(0xFFFFFFFF)
val SwitchTrackTint = Color(0xFFAEAFB4)
val SwitchThumbTint = Color(0xFFFFFFFF)
val SwitchTrackTintDark = Color(0xFF1F3B7C)
val SwitchThumbTintDark = Color(0xFF3772E7)
val LikeColor = Color(0xFFF56B6C)
val BorderColorTextField = Color(0xFFAEAFB4)
val DarkBorderColorTextField = Color(0xFFFFFFFF)

@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
    primary = White,
    primaryVariant = GreyColor,
    onPrimary = TextColor,  // Исправлено на TextColor
    secondary = MainScreenBackgroundColor,
    secondaryVariant = IconTintColor,
    onSecondary = ColorOnSecondaryDay,
    background = White,
    onBackground = TextColor,  // Исправлено на TextColor
    surface = White,
    onSurface = TextColor  // Исправлено на TextColor
)

private val DarkColorPalette = darkColors(
    primary = DarkAppColor,
    primaryVariant = GreyColor,
    onPrimary = White,
    secondary = MainScreenBackgroundColor,
    secondaryVariant = DarkAppColor,
    onSecondary = ColorOnSecondaryNight,
    background = DarkAppColor,
    onBackground = White,
    surface = DarkAppColor,
    onSurface = White
)

@SuppressLint("ConflictingOnColor")
private val MainScreenColorPalette = lightColors(
    primary = MainScreenBackgroundColor,
    primaryVariant = GreyColor,
    onPrimary = TextColor,
    secondary = White,
    secondaryVariant = IconTintColor,
    onSecondary = TextColor,
    background = MainScreenBackgroundColor,
    onBackground = White,
    surface = MainScreenBackgroundColor,
    onSurface = White
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun PlaylistMakerMainScreenTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = MainScreenColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}